package tech.dakenviy.patcher.asm;

import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;
import tech.dakenviy.patcher.asm.mapping.McpMethodMapping;
import tech.dakenviy.patcher.asm.tree.LabelNodeContainer;

import java.util.LinkedList;
import java.util.List;

public class AsmPatch {

    private final McpMethodMapping methodMapping;

    private final List<Step> steps = new LinkedList<>();

    public AsmPatch(McpMethodMapping methodMapping) {
        this.methodMapping = methodMapping;
    }

    public AsmPatch jump(int position) {
        this.steps.add(new Step.Jump(position));

        return this;
    }

    public AsmPatch move(int offset) {
        this.steps.add(new Step.Move(offset));

        return this;
    }

    public AsmPatch jump(InsnList pattern, int ordinal) {
        this.steps.add(new Step.Find(pattern, ordinal));

        return this;
    }

    public AsmPatch jump(InsnList pattern) {
        this.jump(pattern, 0);

        return this;
    }

    public AsmPatch jumpBefore(InsnList pattern) {
        this.jump(pattern);
        this.move(-1);

        return this;
    }

    public AsmPatch jumpAfter(InsnList pattern) {
        this.jump(pattern);
        this.move(pattern.size());

        return this;
    }

    public AsmPatch insert(InsnList instructions) {
        this.steps.add(new Step.Insert(instructions));

        return this;
    }

    public AsmPatch remove(int count) {
        this.steps.add(new Step.Remove(count));

        return this;
    }

    public AsmPatch remove(InsnList pattern) {
        this.jump(pattern);
        this.remove(pattern.size());

        return this;
    }

    public AsmPatch replace(InsnList pattern, InsnList replacement) {
        this.remove(pattern);
        this.insert(replacement);

        return this;
    }

    public AsmPatch label(LabelNodeContainer container) {
        this.steps.add(new Step.GetLabel(container));

        return this;
    }

    public AsmPatch label(InsnList pattern, LabelNodeContainer container) {
        this.jump(pattern);
        this.label(container);

        return this;
    }

    public boolean apply(MethodNode target) {
        int position = 0;

        for (Step step : this.steps) {
            position = step.perform(target.instructions, position);

            if (position == Integer.MIN_VALUE) {
                return false;
            }
        }

        return true;
    }

    public String getTargetClass() {
        return this.methodMapping.getTargetClass();
    }

    public String getMethodName() {
        return this.methodMapping.getName();
    }

    public String getMethodDescriptor() {
        return this.methodMapping.getDescriptor();
    }

    private interface Step {

        int perform(InsnList instructions, int currentPosition);

        class Jump implements Step {

            private final int position;

            public Jump(int position) {
                this.position = position;
            }

            @Override
            public int perform(InsnList instructions, int currentPosition) {
                return this.position != -1 ? this.position : instructions.size() - 1;
            }
        }

        class Move implements Step {

            private final int offset;

            public Move(int offset) {
                this.offset = offset;
            }

            @Override
            public int perform(InsnList instructions, int currentPosition) {
                final int nextPosition = currentPosition + this.offset;

                return nextPosition >= 0 && nextPosition < instructions.size() ? nextPosition : Integer.MIN_VALUE;
            }
        }

        class Find implements Step {

            private final InsnList pattern;
            private final int ordinal;

            public Find(InsnList pattern, int ordinal) {
                this.pattern = pattern;
                this.ordinal = ordinal;
            }

            @Override
            public int perform(InsnList instructions, int currentPosition) {
                final int nextPosition = AsmUtil.findInsn(instructions, this.pattern, this.ordinal, 0);

                return nextPosition >= 0 ? nextPosition : Integer.MIN_VALUE;
            }
        }

        class Insert implements Step {

            private final InsnList instructions;

            public Insert(InsnList instructions) {
                this.instructions = instructions;
            }

            @Override
            public int perform(InsnList instructions, int currentPosition) {
                instructions.insert(instructions.get(currentPosition), this.instructions);

                return currentPosition + this.instructions.size();
            }
        }

        class Remove implements Step {

            private final int count;

            public Remove(int count) {
                this.count = count;
            }

            @Override
            public int perform(InsnList instructions, int currentPosition) {
                AsmUtil.removeInsn(instructions, currentPosition, this.count);

                return currentPosition;
            }
        }

        class GetLabel implements Step {

            private final LabelNodeContainer container;

            public GetLabel(LabelNodeContainer container) {
                this.container = container;
            }

            @Override
            public int perform(InsnList instructions, int currentPosition) {
                final LabelNode labelNode = AsmUtil.findLabel(instructions, currentPosition);

                if (labelNode == null) {
                    return Integer.MIN_VALUE;
                }

                this.container.setLabel(labelNode.getLabel());

                return currentPosition;
            }
        }
    }
}

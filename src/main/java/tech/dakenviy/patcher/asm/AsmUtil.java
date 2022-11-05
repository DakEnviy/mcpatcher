package tech.dakenviy.patcher.asm;

import org.objectweb.asm.tree.*;

import static org.objectweb.asm.tree.AbstractInsnNode.*;

public final class AsmUtil {

    public static MethodNode findMethod(ClassNode classNode, String name, String desc) {
        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals(name) && methodNode.desc.equals(desc)) {
                return methodNode;
            }
        }

        return null;
    }

    public static void removeInsn(InsnList target, int fromIndex, int count) {
        AbstractInsnNode node = target.get(fromIndex);

        while (count > 0) {
            final AbstractInsnNode next = node.getNext();

            target.remove(node);

            node = next;
            --count;
        }
    }

    public static int findInsn(InsnList target, InsnList pattern, int ordinal, int fromIndex) {
        int index = fromIndex;
        AbstractInsnNode node = target.get(fromIndex);
        AbstractInsnNode match = pattern.getFirst();

        while (node != null) {
            if (insnEqual(node, match)) {
                AbstractInsnNode n = node.getNext();
                AbstractInsnNode m = match.getNext();

                while (insnEqual(n, m)) {
                    n = n.getNext();
                    m = m.getNext();
                }

                if (m == null && --ordinal < 0) {
                    return index;
                }
            }

            ++index;
            node = node.getNext();
        }

        return -1;
    }

    public static LabelNode findLabel(InsnList target, int fromIndex) {
        AbstractInsnNode node = target.get(fromIndex);

        while (node != null) {
            if (node.getType() == LABEL) {
                return (LabelNode) node;
            }

            node = node.getPrevious();
        }

        return null;
    }

    // TODO(DakEnviy): Think about other types
    public static boolean insnEqual(AbstractInsnNode node1, AbstractInsnNode node2) {
        if (node1 == null || node2 == null || node1.getOpcode() != node2.getOpcode()) {
            return false;
        }

        switch (node2.getType()) {
            case INT_INSN:
                return intInsnEqual((IntInsnNode) node1, (IntInsnNode) node2);
            case VAR_INSN:
                return varInsnEqual((VarInsnNode) node1, (VarInsnNode) node2);
            case TYPE_INSN:
                return typeInsnEqual((TypeInsnNode) node1, (TypeInsnNode) node2);
            case FIELD_INSN:
                return fieldInsnEqual((FieldInsnNode) node1, (FieldInsnNode) node2);
            case METHOD_INSN:
                return methodInsnEqual((MethodInsnNode) node1, (MethodInsnNode) node2);
            case LDC_INSN:
                return ldcInsnEqual((LdcInsnNode) node1, (LdcInsnNode) node2);
            case IINC_INSN:
                return iincInsnEqual((IincInsnNode) node1, (IincInsnNode) node2);
            default:
                return true;
        }
    }

    private static boolean intInsnEqual(IntInsnNode node1, IntInsnNode node2) {
        return node1.operand == node2.operand;
    }

    private static boolean varInsnEqual(VarInsnNode insn1, VarInsnNode insn2) {
        return insn1.var == insn2.var;
    }

    private static boolean methodInsnEqual(MethodInsnNode insn1, MethodInsnNode insn2) {
        return insn1.owner.equals(insn2.owner) && insn1.name.equals(insn2.name) && insn1.desc.equals(insn2.desc);
    }

    private static boolean fieldInsnEqual(FieldInsnNode insn1, FieldInsnNode insn2) {
        return insn1.owner.equals(insn2.owner) && insn1.name.equals(insn2.name) && insn1.desc.equals(insn2.desc);
    }

    private static boolean ldcInsnEqual(LdcInsnNode insn1, LdcInsnNode insn2) {
        return insn1.cst.equals(insn2.cst);
    }

    private static boolean typeInsnEqual(TypeInsnNode insn1, TypeInsnNode insn2) {
        return insn1.desc.equals(insn2.desc);
    }

    private static boolean iincInsnEqual(IincInsnNode node1, IincInsnNode node2) {
        return node1.var == node2.var && node1.incr == node2.incr;
    }
}

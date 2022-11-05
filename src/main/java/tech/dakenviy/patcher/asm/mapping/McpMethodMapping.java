package tech.dakenviy.patcher.asm.mapping;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.MethodInsnNode;

public class McpMethodMapping extends McpMapping<MethodInsnNode> {

    public McpMethodMapping(String mcpName, String srgName, String owner, String descriptor) {
        super(mcpName, srgName, owner, descriptor);
    }

    public McpMethodMapping(String name, String owner, String descriptor) {
        this(name, name, owner, descriptor);
    }

    @Override
    public MethodInsnNode getInsnNode(int opcode) {
        return new MethodInsnNode(opcode, getOwner(), getName(), getDescriptor(), opcode == Opcodes.INVOKEINTERFACE);
    }
}

package tech.dakenviy.patcher.asm.mapping;

import org.objectweb.asm.tree.FieldInsnNode;

public class McpFieldMapping extends McpMapping<FieldInsnNode> {

    public McpFieldMapping(String mcpName, String srgName, String owner, String descriptor) {
        super(mcpName, srgName, owner, descriptor);
    }

    public McpFieldMapping(String name, String owner, String descriptor) {
        this(name, name, owner, descriptor);
    }

    @Override
    public FieldInsnNode getInsnNode(int opcode) {
        return new FieldInsnNode(opcode, getOwner(), getName(), getDescriptor());
    }
}

package tech.dakenviy.patcher.asm.mapping;

import net.minecraft.launchwrapper.Launch;
import org.objectweb.asm.tree.AbstractInsnNode;

public abstract class McpMapping<T extends AbstractInsnNode> {

    private final boolean isDeobfuscated = (boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");

    private final String mcpName;
    private final String srgName;
    private final String owner;
    private final String descriptor;

    protected McpMapping(String mcpName, String srgName, String owner, String descriptor) {
        this.mcpName = mcpName;
        this.srgName = srgName;
        this.owner = owner;
        this.descriptor = descriptor;
    }

    public String getName() {
        return this.isDeobfuscated ? this.mcpName : this.srgName;
    }

    public String getOwner() {
        return this.owner.replace('.', '/');
    }

    public String getTargetClass() {
        return this.owner.replace('/', '.');
    }

    public String getDescriptor() {
        return this.descriptor;
    }

    public abstract T getInsnNode(int opcode);
}

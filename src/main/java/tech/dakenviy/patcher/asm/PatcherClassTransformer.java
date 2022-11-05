package tech.dakenviy.patcher.asm;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.Launch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public abstract class PatcherClassTransformer implements IClassTransformer {

    private final static Set<String> CLASS_NAMES_TO_PATCH = new HashSet<>();

    private final Logger logger;

    private final Multimap<String, AsmPatch> patches = HashMultimap.create();

    public PatcherClassTransformer(String loggerName) {
        this.logger = LogManager.getLogger(loggerName);

        this.registerPatches();
    }

    public PatcherClassTransformer() {
        this("PatcherClassTransformer");
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        final Collection<AsmPatch> patches = this.patches.get(transformedName);

        if (patches == null || patches.isEmpty()) {
            return basicClass;
        }

        this.logger.info("Patching class {} ({}) with {} patches", transformedName, name, patches.size());

        final ClassNode classNode = new ClassNode();
        final ClassReader classReader = new ClassReader(basicClass);
        classReader.accept(classNode, 0);

        for (AsmPatch patch : patches) {
            final MethodNode target = AsmUtil.findMethod(classNode, patch.getMethodName(), patch.getMethodDescriptor());

            if (target == null) {
                this.logger.error("Method not found : {}:{}{}", patch.getTargetClass(), patch.getMethodName(), patch.getMethodDescriptor());

                continue;
            }

            final boolean isApplied = patch.apply(target);

            if (!isApplied) {
                this.logger.error("Applying patch failed : {}:{}{}", patch.getTargetClass(), patch.getMethodName(), patch.getMethodDescriptor());

                continue;
            }

            this.logger.info("Method patched : {}:{}{}", patch.getTargetClass(), patch.getMethodName(), patch.getMethodDescriptor());
        }

        final ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        classNode.accept(classWriter);

        return classWriter.toByteArray();
    }

    public void registerPatch(AsmPatch patch) {
        final String className = patch.getTargetClass();

        this.patches.put(className, patch);
        CLASS_NAMES_TO_PATCH.add(className);

        this.logger.info("Patch registered for {}", patch.getTargetClass());
    }

    public abstract void registerPatches();

    public static void patchClasses(String loggerName) {
        final Logger logger = LogManager.getLogger(loggerName);

        for (String className : CLASS_NAMES_TO_PATCH) {
            logger.info("Loading class {} to patch", className);

            try {
                Launch.classLoader.loadClass(className);
            } catch (ClassNotFoundException e) {
                logger.error("Class {} not found while applying patches", className);
            }
        }
    }

    public static void patchClasses() {
        patchClasses("PatcherClassTransformer");
    }
}

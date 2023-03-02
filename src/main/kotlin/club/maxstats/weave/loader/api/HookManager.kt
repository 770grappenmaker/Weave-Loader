package club.maxstats.weave.loader.api

import club.maxstats.weave.loader.hooks.registerDefaultHooks
import org.objectweb.asm.*
import org.objectweb.asm.tree.ClassNode
import java.lang.instrument.ClassFileTransformer
import java.security.ProtectionDomain
import java.util.function.BiConsumer
import java.util.function.Consumer

class HookManager {
    private val hooks = mutableListOf<Hook>()

    init {
        registerDefaultHooks()
    }

    fun register(hook: Hook) {
        hooks += hook
    }

    fun register(name: String, block: BiConsumer<ClassNode, Hook.AssemblerConfig>) {
        hooks += object : Hook(name) {
            override fun transform(node: ClassNode, cfg: AssemblerConfig) {
                block.accept(node, cfg)
            }
        }
    }

    fun register(name: String, block: Consumer<ClassNode>) = register(name) { cn, _ ->
        block.accept(cn)
    }

    internal inner class Transformer : ClassFileTransformer {
        override fun transform(
            loader: ClassLoader,
            className: String,
            classBeingRedefined: Class<*>?,
            protectionDomain: ProtectionDomain?,
            originalClass: ByteArray
        ): ByteArray? {
            val hooks = hooks.filter { it.targetClassName == className }
            if (hooks.isEmpty()) return null

            val node = ClassNode()
            val reader = ClassReader(originalClass)
            reader.accept(node, 0)

            val configs = hooks.map { hook ->
                Hook.AssemblerConfig().also { hook.transform(node, it) }
            }
            val computeFrames = configs.any { it.computeFrames }
            val flags = if (computeFrames) ClassWriter.COMPUTE_FRAMES else ClassWriter.COMPUTE_MAXS

            val writer = object : ClassWriter(reader, flags) {
                override fun getClassLoader() = loader
            }
            node.accept(writer)
            return writer.toByteArray()
        }
    }
}
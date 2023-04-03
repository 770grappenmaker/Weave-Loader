package club.maxstats.weave.loader.mixins

import club.maxstats.weave.loader.bootstrap.SafeTransformer
import org.spongepowered.asm.mixin.MixinEnvironment

/**
 * Transformer which delegates the Mixin configuration and application
 * process to the [WeaveMixinService].
 */
internal object WeaveMixinTransformer: SafeTransformer {

    /**
     * @param loader        The classloader to use to load the class.
     * @param className     The name of the class to load.
     * @param originalClass The original class' bytes.
     * @return The transformed class' bytes from `className`.
     */
    override fun transform(loader: ClassLoader, className: String, originalClass: ByteArray): ByteArray? {
        return WeaveMixinService.transformer.transformClass(
            MixinEnvironment.getDefaultEnvironment(),
            className.replace('/', '.'),
            originalClass
        )
    }
}

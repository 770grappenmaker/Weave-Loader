package club.maxstats.weave.loader.hooks

import club.maxstats.weave.loader.api.Hook
import club.maxstats.weave.loader.api.event.CancellableEvent
import club.maxstats.weave.loader.api.event.RenderLivingEvent
import club.maxstats.weave.loader.util.asm
import club.maxstats.weave.loader.util.callEvent
import club.maxstats.weave.loader.util.internalNameOf
import club.maxstats.weave.loader.util.named
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.LabelNode

class RenderLivingEventHook : Hook("net/minecraft/client/renderer/entity/RendererLivingEntity") {
    override fun transform(node: ClassNode, cfg: AssemblerConfig) {
        node.methods.named("doRender").instructions.insert(asm {
            new(internalNameOf<RenderLivingEvent.Pre>())
            dup
            dup
            aload(0)
            aload(1)
            dload(2)
            dload(4)
            dload(6)
            fload(9)
            invokespecial(
                internalNameOf<RenderLivingEvent.Pre>(),
                "<init>",
                "(Lnet/minecraft/client/renderer/entity/RendererLivingEntity;" +
                        "Lnet/minecraft/entity/EntityLivingBase;" +
                        "D" +
                        "D" +
                        "D" +
                        "F)V"
            )
            callEvent()

            val end = LabelNode()

            invokevirtual(internalNameOf<CancellableEvent>(), "isCancelled", "()Z")
            ifeq(end)

            _return

            +end
            f_same()
        })
    }
}

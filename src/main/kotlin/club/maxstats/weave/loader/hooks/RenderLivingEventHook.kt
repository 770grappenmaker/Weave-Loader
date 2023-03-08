package club.maxstats.weave.loader.hooks

import club.maxstats.weave.loader.api.HookManager
import club.maxstats.weave.loader.api.event.CancellableEvent
import club.maxstats.weave.loader.api.event.Event
import club.maxstats.weave.loader.api.event.EventBus
import club.maxstats.weave.loader.api.event.RenderLivingEvent
import club.maxstats.weave.loader.util.asm
import club.maxstats.weave.loader.util.getSingleton
import club.maxstats.weave.loader.util.internalNameOf
import club.maxstats.weave.loader.util.named
import net.minecraft.client.renderer.entity.RendererLivingEntity
import net.minecraft.entity.EntityLivingBase
import org.objectweb.asm.tree.LabelNode

fun HookManager.registerRenderLivingHook() = register("net/minecraft/client/renderer/entity/RendererLivingEntity") {
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
        getSingleton<EventBus>()
        swap

        invokevirtual(
            internalNameOf<EventBus>(),
            "callEvent",
            "(L${internalNameOf<Event>()};)V"
        )

        val end = LabelNode()

        invokevirtual(internalNameOf<CancellableEvent>(), "isCancelled", "()Z")
        ifeq(end)

        _return

        +end
        f_same()
    })
}
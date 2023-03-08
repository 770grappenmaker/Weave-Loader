package club.maxstats.weave.loader.hooks

import club.maxstats.weave.loader.api.HookManager
import club.maxstats.weave.loader.api.event.*
import club.maxstats.weave.loader.util.asm
import club.maxstats.weave.loader.util.getSingleton
import club.maxstats.weave.loader.util.internalNameOf
import club.maxstats.weave.loader.util.named
import net.minecraft.client.gui.GuiScreen
import org.objectweb.asm.tree.LabelNode

fun HookManager.registerGuiOpenHook() = register("net/minecraft/client/Minecraft") {
    node.methods.named("displayGuiScreen").instructions.insert(asm {
        new(internalNameOf<GuiOpenEvent>())
        dup
        dup
        aload(1)
        invokespecial(
            internalNameOf<GuiOpenEvent>(),
            "<init>",
            "(Lnet/minecraft/client/gui/GuiScreen;)V"
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
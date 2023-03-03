package club.maxstats.weave.loader.hooks.impl

import club.maxstats.weave.loader.api.Hook
import club.maxstats.weave.loader.api.event.Event
import club.maxstats.weave.loader.api.event.EventBus
import club.maxstats.weave.loader.api.event.ShutdownEvent
import club.maxstats.weave.loader.util.asm
import club.maxstats.weave.loader.util.getSingleton
import club.maxstats.weave.loader.util.internalNameOf
import club.maxstats.weave.loader.util.named
import org.objectweb.asm.tree.ClassNode

class ShutdownEventHook : Hook("net/minecraft/client/Minecraft") {
    override fun transform(node: ClassNode, cfg: AssemblerConfig) {
        node.methods.named("shutdownMinecraftApplet").instructions.insert(
            asm {
                getSingleton<EventBus>()
                new(internalNameOf<ShutdownEvent>())
                dup
                invokespecial(internalNameOf<ShutdownEvent>(), "<init>", "()V")
                invokevirtual(
                    internalNameOf<EventBus>(),
                    "callEvent",
                    "(L${internalNameOf<Event>()};)V"
                )
            }
        )
    }
}
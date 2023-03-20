package club.maxstats.weave.loader.hooks

import club.maxstats.weave.loader.api.Hook
import club.maxstats.weave.loader.api.event.ServerConnectEvent
import club.maxstats.weave.loader.util.asm
import club.maxstats.weave.loader.util.callEvent
import club.maxstats.weave.loader.util.internalNameOf
import club.maxstats.weave.loader.util.named
import org.objectweb.asm.tree.ClassNode

class ServerConnectEventHook : Hook("net/minecraft/client/multiplayer/GuiConnecting") {
    override fun transform(node: ClassNode, cfg: AssemblerConfig) {
        node.methods.named("connect").instructions.insert(asm {
            new(internalNameOf<ServerConnectEvent>())
            dup
            aload(1)
            iload(2)
            invokespecial(
                internalNameOf<ServerConnectEvent>(),
                "<init>",
                "(Ljava/lang/String;I)V"
            )

            callEvent()
        })
    }
}

package club.maxstats.weave.loader.hooks

import club.maxstats.weave.loader.api.Hook
import club.maxstats.weave.loader.api.event.CancellableEvent
import club.maxstats.weave.loader.api.event.PacketEvent
import club.maxstats.weave.loader.api.util.asm
import club.maxstats.weave.loader.util.callEvent
import club.maxstats.weave.loader.util.internalNameOf
import club.maxstats.weave.loader.util.named
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.LabelNode

internal class PacketEventHook: Hook("net/minecraft/network/NetworkManager") {
    override fun transform(node: ClassNode, cfg: AssemblerConfig) {
        node.methods.filter { it.name == "sendPacket" }.forEach {
            it.instructions.insert(asm {
                new(internalNameOf<PacketEvent.Send>())
                dup; dup
                aload(1)
                invokespecial(internalNameOf<PacketEvent.Send>(), "<init>", "(Lnet/minecraft/network/Packet;)V")
                callEvent()

                val end = LabelNode()
                invokevirtual(internalNameOf<CancellableEvent>(), "isCancelled", "()Z")
                ifeq(end)
                _return
                +end
                f_same()
            })
        }

        node.methods.named("channelRead0").instructions.insert(asm {
            new(internalNameOf<PacketEvent.Receive>())
            dup; dup
            aload(2)
            invokespecial(internalNameOf<PacketEvent.Receive>(), "<init>", "(Lnet/minecraft/network/Packet;)V")
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

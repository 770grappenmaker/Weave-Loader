package club.maxstats.weave.loader.hooks

import club.maxstats.weave.loader.api.HookManager
import club.maxstats.weave.loader.api.event.PlayerListEvent
import club.maxstats.weave.loader.util.*
import org.objectweb.asm.tree.MethodInsnNode

fun HookManager.registerPlayerListEventHook() = register("net/minecraft/client/network/NetHandlerPlayClient") {
    val addInsn = asm {
        new(internalNameOf<PlayerListEvent.Add>())
        dup
        aload(3)
        invokespecial(
            internalNameOf<PlayerListEvent.Add>(),
            "<init>",
            "(Lnet/minecraft/network/play/server/S38PacketPlayerListItem\$AddPlayerData;)V"
        )
        callEvent()
    }

    val removeInsn = asm {
        new(internalNameOf<PlayerListEvent.Remove>())
        dup
        aload(3)
        invokespecial(
            internalNameOf<PlayerListEvent.Remove>(),
            "<init>",
            "(Lnet/minecraft/network/play/server/S38PacketPlayerListItem\$AddPlayerData;)V"
        )
        callEvent()
    }

    val mn = node.methods.named("handlePlayerListItem")
    mn.instructions.insertBefore(mn.instructions.find { it is MethodInsnNode && it.name == "put" }, addInsn)
    mn.instructions.insertBefore(mn.instructions.find { it is MethodInsnNode && it.name == "remove" }, removeInsn)
}

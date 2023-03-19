package club.maxstats.weave.loader.hooks

import club.maxstats.weave.loader.api.Hook
import club.maxstats.weave.loader.api.event.ShutdownEvent
import club.maxstats.weave.loader.util.asm
import club.maxstats.weave.loader.util.callEvent
import club.maxstats.weave.loader.util.getSingleton
import club.maxstats.weave.loader.util.named
import net.minecraft.client.Minecraft
import org.objectweb.asm.tree.ClassNode

class ShutdownEventHook : Hook(Minecraft::class) {
    override fun transform(node: ClassNode, cfg: AssemblerConfig) {
        node.methods.named("shutdownMinecraftApplet").instructions.insert(asm {
            getSingleton<ShutdownEvent>()
            callEvent()
        })
    }
}

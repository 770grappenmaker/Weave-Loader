package club.maxstats.weave.loader.hooks.impl

import club.maxstats.weave.loader.api.Hook
import club.maxstats.weave.loader.api.event.Event
import club.maxstats.weave.loader.api.event.EventBus
import club.maxstats.weave.loader.util.*
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.LabelNode
import org.objectweb.asm.tree.MethodInsnNode

class InputEventHook : Hook("net/minecraft/client/Minecraft") {
    override fun transform(node: ClassNode, cfg: AssemblerConfig) {
        val handler = node.generateMethod(desc = "()V") {
            val endOfIf = LabelNode()

            invokestatic("org/lwjgl/input/Keyboard", "getEventKeyState", "()Z")
            ifeq(endOfIf)

            aload(0)
            getfield("net/minecraft/client/Minecraft", "currentScreen", "Lnet/minecraft/client/gui/GuiScreen;")
            ifnonnull(endOfIf)

            val lambda = LabelNode()
            val end = LabelNode()

            invokestatic("org/lwjgl/input/Keyboard", "getEventKey", "()I")
            ifne(lambda)

            invokestatic("org/lwjgl/input/Keyboard", "getEventCharacter", "()C")
            sipush(256)
            iadd
            goto(end)

            +lambda
            f_same()

            invokestatic("org/lwjgl/input/Keyboard", "getEventKey", "()I")
            +end
            f_same1(Opcodes.INTEGER)

            new("club/maxstats/weave/loader/api/event/InputEvent")
            dup_x1
            swap
            invokespecial("club/maxstats/weave/loader/api/event/InputEvent", "<init>", "(I)V")

            getSingleton<EventBus>()
            swap
            invokevirtual(
                internalNameOf<EventBus>(),
                "callEvent",
                "(L${internalNameOf<Event>()};)V"
            )

            +endOfIf
            f_same()
            _return
        }
        node.methods.add(handler)

        node.methods.named("runTick").let { mn ->
            mn.instructions.insert(
                mn.instructions.find { it is MethodInsnNode && it.name == "dispatchKeypresses" },
                asm {
                    aload(0)
                    invokevirtual(node.name, handler.name, "()V")
                }
            )
        }
    }
}
package net.weavemc.loader.bootstrap

import java.lang.instrument.Instrumentation

@Suppress("UNUSED_PARAMETER")
public fun premain(opt: String?, inst: Instrumentation) {
    if (findVersion() != "1.8.9") {
        println("[Weave] ${findVersion()} not supported, disabling...")
        return
    }

    inst.addTransformer(object : SafeTransformer {
        override fun transform(loader: ClassLoader, className: String, originalClass: ByteArray): ByteArray? {
            // net/minecraft/ false flags on launchwrapper which gets loaded earlier
            if (className.startsWith("net/minecraft/client/")) {
                inst.removeTransformer(this)

                /*
                Load the rest of the loader using Genesis class loader.
                This allows us to access Minecraft's classes throughout the project.
                */
                loader.loadClass("net.weavemc.loader.WeaveLoader")
                    .getDeclaredMethod("init", Instrumentation::class.java)
                    .invoke(null, inst)
            }

            return null
        }
    })
}

private fun findVersion() =
    """--version ([^ ]+)""".toRegex()
        .find(System.getProperty("sun.java.command"))
        ?.groupValues?.get(1)

plugins {
    id("com.github.weave-mc.weave-gradle")
    `java-library`
}

minecraft.version("1.7.10")

dependencies {
    api(project(":api:common"))
    api(libs.bundles.asm)
}

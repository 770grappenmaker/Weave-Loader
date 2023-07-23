plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    kotlin("plugin.lombok")
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

repositories {
    maven("https://repo.spongepowered.org/repository/maven-public/")
}

dependencies {
    compileOnly(libs.mixin)
    api(libs.bundles.asm)
    implementation(libs.kxSer)
    api(project(":api:common"))
}

tasks.jar {
    manifest.attributes(
        "Premain-Class" to "net.weavemc.loader.bootstrap.AgentKt"
    )
}

tasks.shadowJar {
    relocate("org.objectweb.asm", "net.weavemc.asm")
}

tasks.assemble {
    dependsOn("shadowJar")
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
}

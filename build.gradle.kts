plugins {
    `java-library`
    id("io.papermc.paperweight.userdev") version "1.5.4"
}

group = "io.github.thegatesdev"
version = "0.2"
description = "A plugin basis and utility"
java.sourceCompatibility = JavaVersion.VERSION_17

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

dependencies {
    paperweight.paperDevBundle("1.19.4-R0.1-SNAPSHOT")
}

tasks {
    register<Copy>("copyJarToLocalServer") {
        from(jar)
        into("D:\\Coding\\Minecraft\\SERVER\\plugins")
    }

    assemble {
        dependsOn(reobfJar)
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(17)
    }

    processResources {
        filteringCharset = Charsets.UTF_8.name()
        val props = mapOf(
            "name" to project.name,
            "version" to project.version,
            "description" to project.description,
            "apiVersion" to "1.19"
        )
        inputs.properties(props)
        filesMatching("plugin.yml") {
            expand(props)
        }
    }
}
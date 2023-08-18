plugins {
    `java-library`
    id("io.papermc.paperweight.userdev") version "1.5.4"
}

group = "io.github.thegatesdev"
version = "1.0.0"
description = "A plugin basis and utility"
java.sourceCompatibility = JavaVersion.VERSION_17

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

dependencies {
    paperweight.paperDevBundle("1.20-R0.1-SNAPSHOT")
}

tasks {
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
            "apiVersion" to "'1.20'"
        )
        inputs.properties(props)
        filesMatching("plugin.yml") {
            expand(props)
        }
    }

    register<Copy>("pluginJar") {
        from(jar)
        into(buildDir.resolve("pluginJar"))
        rename { "${project.name}.jar" }
    }
}
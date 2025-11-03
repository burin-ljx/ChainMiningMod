plugins {
    id("java")
    alias(libs.plugins.modDevGradle)
    alias(libs.plugins.lombok)
}

group = properties["maven_group"] as String
version = properties["mod_version"] as String

neoForge {
    version = libs.versions.neoForge.get()

    parchment {
        minecraftVersion.set(libs.versions.minecraft.get())
        mappingsVersion.set(libs.versions.parchment.get())
    }

    accessTransformers {
        files("src/main/resources/META-INF/accesstransformer.cfg")
    }

    runs {
        register("client") {
            client()
            systemProperty("neoforge.enabledGameTestNamespaces", properties["mod_id"] as String)
        }

        register("server") {
            server()
            programArgument("--nogui")
            systemProperty("neoforge.enabledGameTestNamespaces", properties["mod_id"] as String)
        }

        register("gameTestServer") {
            type = "gameTestServer"
            systemProperty("neoforge.enabledGameTestNamespaces", properties["mod_id"] as String)
        }

        register("data") {
            data()
            programArguments.addAll(
                "--mod",
                properties["mod_id"] as String,
                "--all",
                "--output",
                file("src/generated/resources/").absolutePath,
                "--existing",
                file("src/main/resources/").absolutePath
            )
        }

        configureEach {
            systemProperty("forge.logging.markers", "REGISTRIES")
            logLevel.set(org.slf4j.event.Level.DEBUG)
        }
    }

    mods {
        register(properties["mod_id"] as String) {
            sourceSet(sourceSets.main.get())
        }
    }
}

tasks.register<Jar>("sourceJar") {
    from(tasks["delombok"])
    dependsOn("delombok")
    archiveClassifier.set("sources")
}

base {
    archivesName.set("${project.name}-${libs.versions.parchment.get()}")
}

fun getConfig(key: String): String {
    return properties[key] as String
}

val contributor = getConfig("contributors")
val supporters = getConfig("supporters")

tasks.withType<ProcessResources>().configureEach {
    var replaceProperties = mapOf(
        "minecraft_version"       to libs.versions.minecraft.get(),
        "minecraft_version_range" to getConfig("minecraft_version_range"),
        "neo_version"             to libs.versions.neoForge.get(),
        "neo_version_range"       to getConfig("neo_version_range"),
        "loader_version_range"    to getConfig("loader_version_range"),
        "mod_id"                  to getConfig("mod_id"),
        "mod_name"                to getConfig("mod_name"),
        "mod_license"             to getConfig("mod_license"),
        "mod_version"             to getConfig("mod_version"),
        "mod_description"         to getConfig("mod_description"),
        "contributors"            to contributor,
        "supporters"              to supporters
    )
    inputs.properties(replaceProperties)

    filesMatching("META-INF/neoforge.mods.toml") {
        expand(replaceProperties)
    }
}

sourceSets.main {
    resources.srcDirs("src/generated/resources")
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(21))

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

lombok {
    version.set("1.18.38")
}
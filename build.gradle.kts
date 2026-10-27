plugins {
    id("java")
    id("com.gtnewhorizons.retrofuturagradle") version "2.0.6"
}

val modId = property("archives_base_name").toString()
val minecraftVersion = property("minecraft_version").toString()
val unimixins = "io.github.legacymoddingmc:unimixins:${property("unimixins_version")}:dev"

group = property("maven_group").toString()
version = "${property("version")}+mc$minecraftVersion"
base.archivesName = modId

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(8)
        vendor = JvmVendorSpec.AZUL
    }
    withSourcesJar()
}

minecraft {
    mcVersion = minecraftVersion
    username = "Developer"
    extraRunJvmArguments.add("-Dmixin.debug.export=true")
    extraTweakClasses.add("org.spongepowered.asm.launch.MixinTweaker")
}

repositories {
    maven("https://nexus.gtnewhorizons.com/repository/public/") { name = "GTNH Maven" }
}

dependencies {
    annotationProcessor("org.ow2.asm:asm-debug-all:5.0.4")
    annotationProcessor("com.google.guava:guava:24.1.1-jre")
    annotationProcessor("com.google.code.gson:gson:2.13.2")
    annotationProcessor(unimixins)
    implementation(modUtils.enableMixins(unimixins, "mixins.$modId.refmap.json"))
}

tasks.processResources {
    val props = mapOf("version" to project.version.toString(), "mcversion" to minecraftVersion)
    inputs.properties(props)
    filesMatching("mcmod.info") { expand(props) }
}

tasks.jar {
    manifest.attributes(
        "TweakClass" to "org.spongepowered.asm.launch.MixinTweaker",
        "MixinConfigs" to "mixins.$modId.json",
        "ForceLoadAsMod" to true,
    )
    from("LICENSE") { rename { "${it}_$modId" } }
}

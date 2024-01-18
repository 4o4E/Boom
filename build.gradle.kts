import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.20"
    kotlin("plugin.serialization") version "1.9.20"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "top.e404"
version = "2.11.0"
val ePluginVersion = "1.2.0"
fun eplugin(module: String, version: String = ePluginVersion) = "top.e404:eplugin-${module}:${version}"

repositories {
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/groups/public/")
    mavenCentral()
    mavenLocal()
}

dependencies {
    // spigot
    compileOnly("org.spigotmc:spigot-api:1.16.5-R0.1-SNAPSHOT")
    // eplugin
    implementation(eplugin("core"))
    implementation(eplugin("serialization"))
    implementation(eplugin("serialization-worldguard"))
    implementation(eplugin("hook-worldguard"))
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }

    processResources {
        filteringCharset = Charsets.UTF_8.name()
        filesMatching("plugin.yml") {
            expand(project.properties)
        }
    }

    shadowJar {
        archiveFileName.set("${project.name}-${project.version}.jar")
        exclude("META-INF/**")
        relocate("org.bstats", "top.e404.boom.relocate.bstats")
        relocate("kotlin", "top.e404.boom.relocate.kotlin")
        relocate("top.eplugin", "top.e404.boom.relocate.eplugin")

        doFirst {
            for (file in File("jar").listFiles() ?: arrayOf()) {
                println("正在删除`${file.name}`")
                file.delete()
            }
        }

        doLast {
            File("jar").mkdirs()
            for (file in File("build/libs").listFiles() ?: arrayOf()) {
                println("正在复制`${file.name}`")
                file.copyTo(File("jar/${file.name}"), true)
            }
        }
    }
}
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10"
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "com.e404"
version = "2.0.0"

repositories {
    maven(url = "https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven(url = "https://oss.sonatype.org/content/groups/public/")
    mavenCentral()
    mavenLocal()
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.13.2-R0.1-SNAPSHOT")
    implementation("org.bstats:bstats-bukkit:2.2.1")
    compileOnly("org.slf4j:slf4j-api:1.7.32")
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.shadowJar {
    archiveFileName.set("Boom - ${project.version}.jar")
    relocate("org.bstats", "com.e404.boom.bstats")
    exclude("META-INF/*")
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

tasks {
    processResources {
        filesMatching("plugin.yml") {
            expand(project.properties)
        }
    }
}
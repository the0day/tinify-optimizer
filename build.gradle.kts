plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.17.3"
    kotlin("jvm") version "1.9.22"
}

group = "com.the0day"
version = "1.0.5"

repositories {
    mavenCentral()
}

intellij {
    version.set("2024.3.5")
    type.set("IU")
    plugins.set(listOf("java"))
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation("org.json:json:20210307")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("com.tinify:tinify:1.8.8")
}

tasks.withType<JavaCompile> {
    options.release.set(21)
}

tasks {
    patchPluginXml {
        changeNotes.set(
            """
            - Update to support latest IDE version
            - Minor UI changes
            """.trimIndent()
        )

        sinceBuild.set("232")
        untilBuild.set("253.*")
    }
    signPlugin {

    }
    publishPlugin {

    }
}

import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm") version "2.2.10"
    id("org.openjfx.javafxplugin") version "0.1.0"
    kotlin("plugin.serialization") version "2.2.10"
    application
}

group = "ru.tsvlad.root.helper"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
    implementation("org.openjfx:javafx-controls:21.0.9")
}

javafx {
    version = "21"
    modules("javafx.controls", "javafx.graphics")
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_21
    }
}

application {
    mainClass.set("ru.tsvlad.root.helper.GameApp")
}

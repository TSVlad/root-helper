plugins {
    kotlin("jvm") version "1.9.0"
    id("org.openjfx.javafxplugin") version "0.0.13"
    kotlin("plugin.serialization") version "1.9.0"
    application
}

group = "ru.tsvlad.root.helper"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
    implementation("org.openjfx:javafx-controls:17.0.7")
}

javafx {
    version = "17"
    modules("javafx.controls", "javafx.graphics")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

application {
    mainClass.set("ru.tsvlad.root.helper.GameApp")
}

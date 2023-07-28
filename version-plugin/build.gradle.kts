import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.20"
    `java-gradle-plugin`
    `maven-publish`
    `kotlin-dsl`
}

group = "com.nmarsollier.versionplugin"
version = "1.6"

repositories {
    mavenCentral()
}

gradlePlugin {
    plugins {
        create("version-plugin") {
            id = "com.nmarsollier.versionplugin"
            implementationClass = "com.nmarsollier.versionplugin.VersionPlugin"
        }
    }
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}

val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
}

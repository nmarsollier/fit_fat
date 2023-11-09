plugins {
    id("com.android.application") version ("7.2.2") apply (false)
    id("com.android.library") version ("7.2.2") apply (false)
    id("org.jetbrains.kotlin.android") version ("1.9.0") apply (false)
    id("com.google.gms.google-services") version "4.3.15" apply false
    id("com.google.firebase.crashlytics") version "2.9.7" apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

buildscript {
    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:8.1.3")
    }
}
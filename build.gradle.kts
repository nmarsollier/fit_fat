plugins {
    id("com.android.application") version ProjectVersions.androidGradlePlugin apply false
    id("com.android.library") version ProjectVersions.androidGradlePlugin apply false
    id("org.jetbrains.kotlin.android") version ProjectVersions.kotlin apply false
    id("com.google.gms.google-services") version ProjectVersions.googleServices apply false
    id("com.google.firebase.crashlytics") version ProjectVersions.firebaseCrashlytics apply false
    id("org.jetbrains.kotlin.plugin.serialization") version LibraryVersions.serialization
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
        classpath("com.android.tools.build:gradle:8.5.2")
    }
}
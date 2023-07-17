plugins {
    id("com.android.application") version ("7.2.2") apply (false)
    id("com.android.library") version ("7.2.2") apply (false)
    id("org.jetbrains.kotlin.android") version ("1.7.10") apply (false)
    id("com.google.gms.google-services") version "4.3.15" apply false
    id("com.google.firebase.crashlytics") version "2.9.7" apply false
}

tasks.register("clean",Delete::class){
    delete(rootProject.buildDir)
}
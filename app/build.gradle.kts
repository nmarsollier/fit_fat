plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("kotlin-parcelize")
    id("com.google.firebase.crashlytics")
    id("com.google.gms.google-services")
    id("org.jetbrains.kotlin.plugin.compose") version ProjectVersions.kotlin
}

android {
    compileSdk = ProjectVersions.compileSdk

    defaultConfig {
        applicationId = "com.nmarsollier.fitfat"
        minSdk = ProjectVersions.minSdk
        targetSdk = ProjectVersions.targetSdk
        versionCode = ProjectVersions.versionCode
        versionName = ProjectVersions.versionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        resourceConfigurations.addAll(listOf("de", "fr", "it", "es", "nl", "pt", "sv"))
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = ProjectVersions.kotlinCompilerExtensionVersion
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-alpha"
            isMinifyEnabled = false
            isDebuggable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
            buildConfigField(
                "String",
                "WEB_CLIENT_ID",
                "\"823698859435-a2r0a6d7migoop4a9r8qa5s8connu7l8.apps.googleusercontent.com\""
            )
        }

        release {
            isMinifyEnabled = true
            isDebuggable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
            buildConfigField(
                "String",
                "WEB_CLIENT_ID",
                "\"823698859435-a2r0a6d7migoop4a9r8qa5s8connu7l8.apps.googleusercontent.com\""
            )
        }
    }
    compileOptions {
        sourceCompatibility = ProjectVersions.sourceCompatibility
        targetCompatibility = ProjectVersions.sourceCompatibility
    }
    kotlinOptions {
        jvmTarget = ProjectVersions.jvmTarget
    }

    composeOptions {
        kotlinCompilerExtensionVersion = ProjectVersions.kotlin
    }
    namespace = "com.nmarsollier.fitfat"
}

dependencies {
    // Compose
    val composeBom = platform("androidx.compose:compose-bom:${LibraryVersions.composeBom}")

    implementation(composeBom)
    implementation("androidx.activity:activity-compose")
    implementation("androidx.compose.runtime:runtime-livedata")
    implementation("androidx.compose.ui:ui-tooling")
    implementation("androidx.compose.ui:ui-tooling-preview")

    implementation("androidx.arch.core:core-runtime:${LibraryVersions.coreRuntime}")
    implementation("androidx.compose.material3:material3:${LibraryVersions.material}")
    implementation("androidx.core:core-ktx:${LibraryVersions.coreKtx}")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:${LibraryVersions.lifecycleViewModel}")
    implementation("com.google.accompanist:accompanist-navigation-material:${LibraryVersions.accompanist}")
    implementation("com.google.code.gson:gson:${LibraryVersions.gson}")
    implementation("io.insert-koin:koin-androidx-compose:${LibraryVersions.koinCompose}")

    // Firebase login
    implementation("com.google.android.gms:play-services-auth:${LibraryVersions.playServicesAuth}")
    implementation(platform("com.google.firebase:firebase-bom:${LibraryVersions.firebaseBom}"))
    implementation("com.google.firebase:firebase-crashlytics-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-auth-ktx")

    // Kotlin libs
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:${LibraryVersions.stdLib}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${LibraryVersions.coroutines}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${LibraryVersions.coroutines}")

    // Hello Charts
    implementation("com.github.lecho:hellocharts-library:${LibraryVersions.helloCharts}")

    // Android room
    implementation("androidx.room:room-ktx:${LibraryVersions.room}")
    kapt("androidx.room:room-compiler:${LibraryVersions.room}")

    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test-jvm:${LibraryVersions.coroutinesTest}")
    testImplementation("junit:junit:${LibraryVersions.jUnit}")
    testImplementation("io.mockk:mockk-android:${LibraryVersions.mockk}")
    testImplementation("io.mockk:mockk-agent:${LibraryVersions.mockk}")
    testImplementation("app.cash.turbine:turbine:${LibraryVersions.turbine}")
    testImplementation("io.insert-koin:koin-android-test:${LibraryVersions.koinTest}")
    testImplementation("io.insert-koin:koin-test-junit4:${LibraryVersions.koinTest}")

    androidTestImplementation("androidx.test.ext:junit:${LibraryVersions.extJunit}")
    androidTestImplementation("androidx.test.espresso:espresso-core:${LibraryVersions.espresso}")
}

configurations.all {
    resolutionStrategy {}
}

kapt {
    correctErrorTypes = true
}

buildscript {
    repositories {
        google()
        mavenCentral()
    }
}

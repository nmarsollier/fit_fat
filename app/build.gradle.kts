plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("kotlin-parcelize")
    id("com.google.firebase.crashlytics")
    id("com.google.gms.google-services")
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
            buildConfigField("String", "WEB_CLIENT_ID", "\"823698859435-a2r0a6d7migoop4a9r8qa5s8connu7l8.apps.googleusercontent.com\"")
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("String", "WEB_CLIENT_ID", "\"823698859435-a2r0a6d7migoop4a9r8qa5s8connu7l8.apps.googleusercontent.com\"")
        }
    }
    compileOptions {
        sourceCompatibility = ProjectVersions.sourceCompatibility
        targetCompatibility = ProjectVersions.sourceCompatibility
    }
    kotlinOptions {
        jvmTarget = ProjectVersions.jvmTarget
    }
    namespace = "com.nmarsollier.fitfat"
}

dependencies {
    // Compose
    val composeBom = platform("androidx.compose:compose-bom:${LibraryVersions.composeBom}")

    implementation(composeBom)
    implementation("androidx.compose.material:material:${LibraryVersions.compose}")
    implementation("androidx.compose.runtime:runtime-livedata:${LibraryVersions.compose}")
    implementation("androidx.customview:customview-poolingcontainer:${LibraryVersions.poolingContainer}")
    implementation("androidx.compose.ui:ui-tooling")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.lifecycle:lifecycle-runtime-compose")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:${LibraryVersions.lifecycleViewModel}")
    implementation("androidx.activity:activity-compose")
    implementation("io.insert-koin:koin-androidx-compose:${LibraryVersions.koinCompose}")
    implementation("androidx.activity:activity-ktx:${LibraryVersions.activityKtx}")
    implementation("androidx.core:core-ktx:${LibraryVersions.coreKtx}")
    implementation("androidx.arch.core:core-runtime:${LibraryVersions.coreRuntime}")
    implementation("com.google.errorprone:error_prone_annotations:${LibraryVersions.errorProneAnnotations}")
    implementation("com.google.code.gson:gson:${LibraryVersions.gson}")

    implementation("com.google.android.material:material:${LibraryVersions.material}")

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
    implementation("androidx.room:room-runtime:${LibraryVersions.room}")
    kapt("androidx.room:room-compiler:${LibraryVersions.room}")
    debugImplementation("im.dino:dbinspector:${LibraryVersions.dbInspector}")

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
    resolutionStrategy {
    }
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

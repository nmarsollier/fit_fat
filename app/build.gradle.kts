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
        viewBinding = true
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-alpha"
            isMinifyEnabled = false
            buildConfigField(
                "String",
                "WEB_CLIENT_ID",
                "\"823698859435-a2r0a6d7migoop4a9r8qa5s8connu7l8.apps.googleusercontent.com\""
            )
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField(
                "String",
                "WEB_CLIENT_ID",
                "\"823698859435-a2r0a6d7migoop4a9r8qa5s8connu7l8.apps.googleusercontent.com\""
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    namespace = "com.nmarsollier.fitfat"
}

dependencies {
    implementation(kotlin("reflect"))

    androidTestImplementation("androidx.test.espresso:espresso-core:${LibraryVersions.espresso}")
    androidTestImplementation("androidx.test.ext:junit:${LibraryVersions.extJunit}")

    implementation("androidx.activity:activity-ktx:${LibraryVersions.activityKtx}")
    implementation("androidx.appcompat:appcompat:${LibraryVersions.appCompat}")
    implementation("androidx.arch.core:core-runtime:${LibraryVersions.coreRuntime}")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.core:core-ktx:${LibraryVersions.coreKtx}")
    implementation("androidx.customview:customview-poolingcontainer:${LibraryVersions.poolingContainer}")
    implementation("androidx.room:room-ktx:${LibraryVersions.room}")
    implementation("androidx.room:room-runtime:${LibraryVersions.room}")
    implementation("com.github.lecho:hellocharts-library:${LibraryVersions.helloCharts}")
    implementation("com.google.accompanist:accompanist-navigation-material:${LibraryVersions.accompanist}")
    implementation("com.google.android.gms:play-services-auth:${LibraryVersions.playServicesAuth}")
    implementation("com.google.android.material:material:${LibraryVersions.material}")
    implementation("com.google.code.gson:gson:${LibraryVersions.gson}")
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-crashlytics-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("io.insert-koin:koin-androidx-compose:${LibraryVersions.koinCompose}")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:${LibraryVersions.stdLib}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${LibraryVersions.coroutines}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${LibraryVersions.coroutines}")
    implementation(platform("com.google.firebase:firebase-bom:${LibraryVersions.firebaseBom}"))
    kapt("androidx.room:room-compiler:${LibraryVersions.room}")

    testImplementation("app.cash.turbine:turbine:${LibraryVersions.turbine}")
    testImplementation("io.insert-koin:koin-android-test:${LibraryVersions.koinTest}")
    testImplementation("io.insert-koin:koin-test-junit4:${LibraryVersions.koinTest}")
    testImplementation("io.mockk:mockk-agent:${LibraryVersions.mockk}")
    testImplementation("io.mockk:mockk-android:${LibraryVersions.mockk}")
    testImplementation("junit:junit:${LibraryVersions.jUnit}")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test-jvm:${LibraryVersions.coroutinesTest}")
}

kapt {
    correctErrorTypes = true
}

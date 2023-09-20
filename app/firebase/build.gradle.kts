plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.nmarsollier.fitfat.firebase"
    compileSdk = ProjectVersions.compileSdk

    defaultConfig {
        minSdk = ProjectVersions.minSdk
        targetSdk = ProjectVersions.targetSdk

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = ProjectVersions.sourceCompatibility
        targetCompatibility = ProjectVersions.sourceCompatibility
    }
    kotlinOptions {
        jvmTarget = ProjectVersions.jvmTarget
    }
}

dependencies {
    implementation("androidx.core:core-ktx:${LibraryVersions.coreKtx}")
    implementation(project(":app:utils"))

    implementation("io.insert-koin:koin-androidx-compose:${LibraryVersions.koinCompose}")

    implementation("com.google.android.gms:play-services-auth:${LibraryVersions.playServicesAuth}")
    implementation(platform("com.google.firebase:firebase-bom:${LibraryVersions.firebaseBom}"))
    implementation("com.google.firebase:firebase-crashlytics-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-auth-ktx")


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
plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-kapt")
    id("kotlin-parcelize")
}

android {
    namespace = "com.nmarsollier.fitfat.utils"
    compileSdk = ProjectVersions.compileSdk

    defaultConfig {
        minSdk = ProjectVersions.minSdk
        targetSdk = ProjectVersions.targetSdk

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = ProjectVersions.kotlinCompilerExtensionVersion
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
    // Compose
    val composeBom = platform("androidx.compose:compose-bom:${LibraryVersions.composeBom}")

    implementation(composeBom)
    implementation("androidx.compose.material:material:${LibraryVersions.compose}")
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

    implementation("com.google.android.material:material:${LibraryVersions.material}")
    implementation("com.google.code.gson:gson:${LibraryVersions.gson}")

    // Kotlin libs
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:${LibraryVersions.stdLib}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${LibraryVersions.coroutines}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${LibraryVersions.coroutines}")


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
plugins {
    id(Plugins.application)
    id(Plugins.kotlin)
    id(Plugins.kapt)
    id(Plugins.parcelize)
    id(Plugins.crashlytics)
    id(Plugins.googleServices)
}

android {
    compileSdk = 32

    defaultConfig {
        applicationId = "com.nmarsollier.fitfat"
        minSdk = 28
        targetSdk = 32
        versionCode = 1
        versionName = "1.0"

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
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
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
}

dependencies {

    implementation(AndroidXLibraries.roomKtx)
    implementation(AndroidXLibraries.roomRuntime)
    kapt(AndroidXLibraries.roomCompiler)

    implementation(AndroidXLibraries.appCompat)
    implementation(AndroidXLibraries.constraintLayout)
    implementation(AndroidXLibraries.coreKtx)
    implementation(AndroidXLibraries.liveData)
    implementation(AndroidXLibraries.viewModelKtx)
    implementation(AndroidXLibraries.fragmentKtx)

    implementation(GoogleLibraries.googleAuth)
    implementation(GoogleLibraries.material)

    implementation(platform(FirebaseLibraries.bom))
    implementation(FirebaseLibraries.crashlitics)
    implementation(FirebaseLibraries.analytics)
    implementation(FirebaseLibraries.firestore)
    implementation(FirebaseLibraries.auth)

    implementation(ThirdPartyLibraries.helloCharts)

    implementation(JetbrainsLibraries.stdLib)
    implementation(JetbrainsLibraries.coroutinesAndroid)
    implementation(JetbrainsLibraries.coroutinesCore)
    debugImplementation(ThirdPartyLibraries.dbInspector)

    testImplementation(TestLibraries.jUnit)
    androidTestImplementation(TestLibraries.extJUnit)
    androidTestImplementation(TestLibraries.espressoCore)
}
private object Versions {
    const val extJUnitVersion = "1.1.1"
    const val espressoCoreVersion = "3.1.0"
    const val roomKtxVersion = "2.4.2"
    const val appCompat = "1.5.1"
    const val constraintLayoutVersion = "2.1.4"
    const val coreKtxVersion = "1.8.0"
    const val liveDataVersion = "2.5.0"
    const val viewModelKtxVersion = "2.4.0"
    const val fragmentKtxVersion = "1.3.2"
    const val googleAuthVersion = "20.6.0"
    const val materialVersion = "1.9.0"
    const val firebaseBomVersion = "32.2.0"
    const val helloChartVersion = "1.5.8@aar"
    const val dbInspectorVersion = "3.4.1@aar"
    const val stdLibVersion = "1.7.10"
    const val coroutinesVersion = "1.6.0"
    const val jUnitVersion = "4.13.2"
}

object AndroidXLibraries {
    const val roomKtx = "androidx.room:room-ktx:${Versions.roomKtxVersion}"
    const val roomRuntime = "androidx.room:room-runtime:${Versions.roomKtxVersion}"
    const val roomCompiler = "androidx.room:room-compiler:${Versions.roomKtxVersion}"

    const val coreKtx = "androidx.core:core-ktx:${Versions.coreKtxVersion}"

    const val appCompat = "androidx.appcompat:appcompat:${Versions.appCompat}"
    const val constraintLayout =
        "androidx.constraintlayout:constraintlayout:${Versions.constraintLayoutVersion}"

    const val liveData = "androidx.lifecycle:lifecycle-livedata:${Versions.liveDataVersion}"
    const val viewModelKtx =
        "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.viewModelKtxVersion}"
    const val fragmentKtx = "androidx.fragment:fragment-ktx:${Versions.fragmentKtxVersion}"
}

object GoogleLibraries {
    const val googleAuth = "com.google.android.gms:play-services-auth:${Versions.googleAuthVersion}"
    const val material = "com.google.android.material:material:${Versions.materialVersion}"
}

object FirebaseLibraries {
    const val bom = "com.google.firebase:firebase-bom:${Versions.firebaseBomVersion}"
    const val crashlitics = "com.google.firebase:firebase-crashlytics-ktx"
    const val analytics = "com.google.firebase:firebase-analytics-ktx"
    const val firestore = "com.google.firebase:firebase-firestore-ktx"
    const val auth = "com.google.firebase:firebase-auth-ktx"
}

object ThirdPartyLibraries {
    const val helloCharts = "com.github.lecho:hellocharts-library:${Versions.helloChartVersion}"
    const val dbInspector = "im.dino:dbinspector:${Versions.dbInspectorVersion}"
}

object JetbrainsLibraries {
    const val stdLib = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Versions.stdLibVersion}"
    const val coroutinesAndroid =
        "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutinesVersion}"
    const val coroutinesCore =
        "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutinesVersion}"
}

object TestLibraries {
    const val jUnit = "junit:junit:${Versions.jUnitVersion}"
    const val extJUnit = "androidx.test.ext:junit:${Versions.extJUnitVersion}"
    const val espressoCore = "androidx.test.espresso:espresso-core:${Versions.espressoCoreVersion}"
}

object Plugins{
    const val application = "com.android.application"
    const val kotlin = "kotlin-android"
    const val kapt = "kotlin-kapt"
    const val parcelize = "kotlin-parcelize"
    const val crashlytics = "com.google.firebase.crashlytics"
    const val googleServices = "com.google.gms.google-services"
}

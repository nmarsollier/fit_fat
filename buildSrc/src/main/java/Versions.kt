import org.gradle.api.*

/*
 * Copyright (c) 2023 PayPal, Inc.
 *
 * All rights reserved.
 *
 * THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY
 * KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A
 * PARTICULAR PURPOSE.
 */
object ProjectVersions {
    const val compileSdk = 34
    const val targetSdk = 33
    const val minSdk = 28
    const val versionCode = 13
    const val versionName = "2.3"
    const val jvmTarget = "17"
    val sourceCompatibility = JavaVersion.VERSION_17
    const val kotlinCompilerExtensionVersion = "1.5.2"

    const val androidGradlePlugin = "8.0.2"
    const val kotlin = "2.0.20"
    const val googleServices = "4.3.15"
    const val firebaseCrashlytics = "2.9.7"
}

object LibraryVersions {
    const val composeBom = "2023.08.00"

    const val compose = "1.7.2"
    const val lifecycleViewModel = "2.8.6"
    const val koinCompose = "3.5.3"
    const val poolingContainer = "1.0.0"

    const val activityKtx = "1.9.2"
    const val coreKtx = "1.13.1"
    const val coreRuntime = "2.2.0"
    const val errorProneAnnotations = "2.28.0"

    const val room = "2.6.1"

    const val material = "1.3.0"
    const val gson = "2.10.1"
    const val helloCharts = "1.5.8@aar"
    const val accompanist = "0.34.0"

    const val playServicesAuth = "21.2.0"
    const val firebaseBom = "33.3.0"

    const val stdLib = "1.9.23"
    const val coroutines = "1.7.3"

    const val dbInspector = "3.4.1@aar"

    const val coroutinesTest = "1.7.3"
    const val jUnit = "4.13.2"

    const val mockk = "1.13.9"
    const val turbine = "1.0.0"

    const val koinTest = "3.5.3"

    const val extJunit = "1.2.1"
    const val espresso = "3.6.1"
}

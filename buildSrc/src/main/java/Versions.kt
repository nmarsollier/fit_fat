import org.gradle.api.JavaVersion

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
    const val versionCode = 1
    const val versionName = "1.9"
    const val jvmTarget = "17"
    val sourceCompatibility = JavaVersion.VERSION_17
    const val kotlinCompilerExtensionVersion = "1.5.2"
}

object LibraryVersions {
    const val composeBom = "2023.08.00"

    const val compose = "1.5.1"
    const val lifecycleViewModel = "2.6.2"
    const val koinCompose = "3.4.6"

    const val activityKtx = "1.7.2"
    const val coreKtx = "1.12.0"
    const val coreRuntime = "2.2.0"
    const val errorProneAnnotations = "2.15.0"

    const val room = "2.5.2"

    const val material = "1.9.0"
    const val gson = "2.10.1"
    const val helloCharts = "1.5.8@aar"

    const val playServicesAuth = "20.7.0"
    const val firebaseBom = "32.2.0"

    const val stdLib = "1.9.10"
    const val coroutines = "1.7.3"

    const val dbInspector = "3.4.1@aar"

    const val coroutinesTest = "1.6.4"
    const val jUnit = "4.13.2"

    const val mockk = "1.13.7"
    const val turbine = "1.0.0"

    const val koinTest = "3.4.3"

    const val extJunit = "1.1.5"
    const val espresso = "3.5.1"
}

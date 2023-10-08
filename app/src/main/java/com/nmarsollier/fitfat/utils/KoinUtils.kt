package com.nmarsollier.fitfat.utils

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.nmarsollier.fitfat.model.koinDatabaseModule
import com.nmarsollier.fitfat.ui.koinUiModule
import com.nmarsollier.fitfat.useCases.koinUseCaseModules
import org.koin.android.ext.koin.androidContext
import org.koin.compose.KoinApplication

@Composable
fun KoinPreview(composable: @Composable () -> Unit) {
    val context = LocalContext.current

    MaterialTheme {
        KoinApplication(application = {
            androidContext(context)
            modules(koinDatabaseModule, koinUiModule, koinUseCaseModules)
        }, composable)
    }
}

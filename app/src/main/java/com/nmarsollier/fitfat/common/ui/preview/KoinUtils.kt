package com.nmarsollier.fitfat.common.ui.preview

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.nmarsollier.fitfat.common.koinUtilsModule
import com.nmarsollier.fitfat.common.ui.theme.AppColors
import org.koin.android.ext.koin.androidContext
import org.koin.compose.KoinApplication

@Composable
fun KoinPreview(composable: @Composable () -> Unit) {
    val context = LocalContext.current

    MaterialTheme(colors = AppColors) {
        KoinApplication(application = {
            androidContext(context)
            modules(koinUtilsModule)
        }, composable)
    }
}

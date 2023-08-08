package com.nmarsollier.fitfat.ui.common.preview

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.*
import com.nmarsollier.fitfat.ui.common.theme.*
import com.nmarsollier.fitfat.utils.*
import org.koin.android.ext.koin.*
import org.koin.compose.*

@Composable
fun KoinPreview(composable: @Composable () -> Unit) {
    val context = LocalContext.current

    MaterialTheme(
        colorScheme = LightColorScheme
    ) {
        KoinApplication(application = {
            androidContext(context)
            modules(koinUtilsModule)
        }, composable)
    }
}

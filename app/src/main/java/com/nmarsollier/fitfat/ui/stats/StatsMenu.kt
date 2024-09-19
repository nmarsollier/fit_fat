package com.nmarsollier.fitfat.ui.stats

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.nmarsollier.fitfat.R
import com.nmarsollier.fitfat.ui.common.preview.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsMenu() {
    TopAppBar(
        modifier = Modifier.height(48.dp),
        title = {
            Row(
                modifier = Modifier.fillMaxHeight(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(R.string.home_progress_title))
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}

@Preview
@Composable
private fun OptionsMenuPreview() {
    KoinPreview {
        Column {
            StatsMenu()
        }
    }
}
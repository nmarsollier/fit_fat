package com.nmarsollier.fitfat.ui.userSettings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
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
fun OptionsMenu(
    onSaveClick: () -> Unit
) {
    TopAppBar(
        modifier = Modifier.height(48.dp),
        title = {
            Row(
                modifier = Modifier.fillMaxHeight(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(R.string.home_options_title))
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
        ),
        actions = {
            IconButton(onClick = {
                onSaveClick()
            }) {
                Icon(
                    Icons.Default.Check,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    contentDescription = stringResource(id = R.string.save_dialog_title)
                )
            }
        })
}

@Preview
@Composable
private fun OptionsMenuPreview() {
    KoinPreview {
        Column {
            OptionsMenu {}
        }
    }
}

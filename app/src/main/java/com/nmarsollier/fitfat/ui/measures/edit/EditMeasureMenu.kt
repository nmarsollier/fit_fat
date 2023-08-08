package com.nmarsollier.fitfat.ui.measures.edit

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.nmarsollier.fitfat.R
import com.nmarsollier.fitfat.models.measures.*
import com.nmarsollier.fitfat.models.userSettings.*
import com.nmarsollier.fitfat.ui.common.preview.*
import com.nmarsollier.fitfat.ui.measures.*
import com.nmarsollier.fitfat.ui.userSettings.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditMeasureMenu(
    state: EditMeasureState, eventHandler: (EditMeasureAction) -> Unit
) {
    val saveEnabled = (state as? EditMeasureState.Ready)?.isSaveEnabled == true
    TopAppBar(
        modifier = Modifier.height(48.dp),
        title = {
            Row(
                modifier = Modifier.fillMaxHeight(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(R.string.new_measure_title))
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
        ),
        navigationIcon = {
            IconButton(onClick = {
                eventHandler(EditMeasureAction.Close)
            }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "")
            }
        }, actions = {
            if (!state.currentReadOnly) {
                IconButton(onClick = {
                    if (saveEnabled) {
                        eventHandler(EditMeasureAction.SaveMeasure)
                    }
                }) {
                    Icon(
                        Icons.Default.Check, tint = if (saveEnabled) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.secondaryContainer
                        }, contentDescription = ""
                    )
                }
            }
        })
}

@Preview
@Composable
private fun EditMeasureMenuPreview() {
    KoinPreview {
        Column {
            EditMeasureMenu(
                EditMeasureState.Ready(
                    isSaveEnabled = true,
                    userSettings = UserSettings.Samples.simpleData,
                    measure = Measure.Samples.simpleData[0],
                    showHelp = null,
                    showMeasureMethod = false,
                    readOnly = false
                )
            ) {}

            EditMeasureMenu(
                EditMeasureState.Ready(
                    isSaveEnabled = false,
                    userSettings = UserSettings.Samples.simpleData,
                    measure = Measure.Samples.simpleData[0],
                    showHelp = null,
                    showMeasureMethod = false,
                    readOnly = false
                )
            ) {}
        }
    }
}

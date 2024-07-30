package com.nmarsollier.fitfat.measures.ui.edit

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.nmarsollier.fitfat.R
import com.nmarsollier.fitfat.common.ui.preview.KoinPreview
import com.nmarsollier.fitfat.common.ui.theme.AppColors
import com.nmarsollier.fitfat.measures.model.Measure
import com.nmarsollier.fitfat.measures.samples.Samples
import com.nmarsollier.fitfat.userSettings.model.UserSettings
import com.nmarsollier.fitfat.userSettings.samples.Samples

@Composable
fun EditMeasureMenu(
    state: EditMeasureState, eventHandler: (EditMeasureAction) -> Unit
) {
    TopAppBar(title = { Text(stringResource(R.string.new_measure_title)) }, navigationIcon = {
        IconButton(onClick = {
            eventHandler(EditMeasureAction.Close)
        }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, "")
        }
    }, actions = {
        if (!state.currentReadOnly) {
            IconButton(onClick = {
                eventHandler(EditMeasureAction.SaveMeasure)
            }) {
                Icon(Icons.Default.Check, tint = AppColors.onPrimary, contentDescription = "")
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
                    userSettings = UserSettings.Samples.simpleData.value,
                    measure = Measure.Samples.simpleData[0].value,
                    showHelp = null,
                    showMeasureMethod = false,
                    readOnly = false
                ), EditMeasureViewModel.Samples::reduce
            )
        }
    }
}

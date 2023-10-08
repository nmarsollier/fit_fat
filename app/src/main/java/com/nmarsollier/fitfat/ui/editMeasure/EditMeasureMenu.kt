package com.nmarsollier.fitfat.ui.editMeasure

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.nmarsollier.fitfat.R
import com.nmarsollier.fitfat.model.measures.Measure
import com.nmarsollier.fitfat.model.userSettings.UserSettingsEntity
import com.nmarsollier.fitfat.utils.KoinPreview
import com.nmarsollier.fitfat.utils.Samples

@Composable
fun EditMeasureMenu(
    state: EditMeasureState, eventHandler: EditMeasureReducer
) {
    TopAppBar(title = { Text(stringResource(R.string.new_measure_title)) }, navigationIcon = {
        IconButton(onClick = {
            eventHandler.close()
        }) {
            Icon(Icons.Default.ArrowBack, "")
        }
    }, actions = {
        if (!state.currentReadOnly) {
            IconButton(onClick = {
                eventHandler.saveMeasure()
            }) {
                Icon(Icons.Default.Check, "")
            }
        }
    })
}

@Preview
@Composable
fun EditMeasureMenuPreview() {
    KoinPreview {
        Column {
            EditMeasureMenu(
                EditMeasureState.Ready(
                    userSettingsEntity = UserSettingsEntity.Samples.simpleData,
                    measure = Measure.Samples.simpleData[0],
                    showHelp = null,
                    showMethod = false,
                    readOnly = false
                ), EditMeasureViewModel.Samples.reducer()
            )
        }
    }
}

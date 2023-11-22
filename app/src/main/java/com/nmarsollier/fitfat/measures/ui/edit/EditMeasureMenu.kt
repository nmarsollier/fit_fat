package com.nmarsollier.fitfat.measures.ui.edit

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
import com.nmarsollier.fitfat.common.ui.preview.KoinPreview
import com.nmarsollier.fitfat.common.ui.viewModel.Reducer
import com.nmarsollier.fitfat.measures.model.Measure
import com.nmarsollier.fitfat.measures.samples.Samples
import com.nmarsollier.fitfat.userSettings.model.UserSettings
import com.nmarsollier.fitfat.userSettings.samples.Samples

@Composable
fun EditMeasureMenu(
    state: EditMeasureState, eventHandler: Reducer<EditMeasureEvent>
) {
    TopAppBar(title = { Text(stringResource(R.string.new_measure_title)) }, navigationIcon = {
        IconButton(onClick = {
            eventHandler.reduce(EditMeasureEvent.Close)
        }) {
            Icon(Icons.Default.ArrowBack, "")
        }
    }, actions = {
        if (!state.currentReadOnly) {
            IconButton(onClick = {
                eventHandler.reduce(EditMeasureEvent.SaveMeasure)
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
                    userSettings = UserSettings.Samples.simpleData.value,
                    measure = Measure.Samples.simpleData[0].value,
                    showHelp = null,
                    showMethod = false,
                    readOnly = false
                ), EditMeasureViewModel.Samples.reducer()
            )
        }
    }
}
package com.nmarsollier.fitfat.userSettings.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.nmarsollier.fitfat.R
import com.nmarsollier.fitfat.common.ui.viewModel.Reducer
import com.nmarsollier.fitfat.userSettings.samples.Samples

@Composable
fun OptionsMenu(
    reducer: Reducer<OptionsEvent>
) {
    TopAppBar(title = { Text(stringResource(R.string.home_options_title)) }, actions = {
        IconButton(onClick = {
            reducer.reduce(OptionsEvent.SaveSettings)
        }) {
            Icon(Icons.Default.Check, stringResource(id = R.string.save_dialog_title))
        }
    })
}

@Preview
@Composable
fun OptionsMenuPreview() {
    com.nmarsollier.fitfat.common.ui.preview.KoinPreview {
        Column {
            OptionsMenu(OptionsViewModel.Samples.reducer())
        }
    }
}

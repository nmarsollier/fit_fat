package com.nmarsollier.fitfat.ui.options

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
import com.nmarsollier.fitfat.utils.KoinPreview
import com.nmarsollier.fitfat.utils.Samples

@Composable
fun OptionsMenu(
    reducer: OptionsReducer
) {
    TopAppBar(title = { Text(stringResource(R.string.home_options_title)) }, actions = {
        IconButton(onClick = {
            reducer.saveSettings()
        }) {
            Icon(Icons.Default.Check, stringResource(id = R.string.save_dialog_title))
        }
    })
}

@Preview
@Composable
fun OptionsMenuPreview() {
    KoinPreview {
        Column {
            OptionsMenu(OptionsViewModel.Samples.reducer())
        }
    }
}

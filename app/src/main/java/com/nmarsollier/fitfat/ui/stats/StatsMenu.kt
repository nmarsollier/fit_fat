package com.nmarsollier.fitfat.ui.stats

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.nmarsollier.fitfat.R
import com.nmarsollier.fitfat.utils.KoinPreview

@Composable
fun StatsMenu() {
    TopAppBar(title = {
        Text(
            stringResource(R.string.home_progress_title)
        )
    })
}

@Preview
@Composable
fun OptionsMenuPreview() {
    KoinPreview {
        Column {
            StatsMenu()
        }
    }
}

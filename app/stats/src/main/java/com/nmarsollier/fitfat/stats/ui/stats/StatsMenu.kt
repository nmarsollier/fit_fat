package com.nmarsollier.fitfat.stats.ui.stats

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.nmarsollier.fitfat.stats.R
import com.nmarsollier.fitfat.utils.ui.preview.KoinPreview

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

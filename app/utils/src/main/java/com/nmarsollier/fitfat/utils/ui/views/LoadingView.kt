package com.nmarsollier.fitfat.utils.ui.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nmarsollier.fitfat.utils.ui.preview.KoinPreview

@Composable
fun LoadingView() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .width(
                50.dp
            )
    ) {
        CircularProgressIndicator(
            modifier = Modifier.width(80.dp),
            strokeWidth = 7.dp
        )
    }
}

@Preview
@Composable
fun LoadingViewPreview() {
    KoinPreview {
        LoadingView()
    }
}


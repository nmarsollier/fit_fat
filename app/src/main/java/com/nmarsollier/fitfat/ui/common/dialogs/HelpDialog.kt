package com.nmarsollier.fitfat.ui.common.dialogs

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.nmarsollier.fitfat.ui.common.preview.KoinPreview

@Composable
fun HelpDialog(helpRes: Int, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = { onDismiss() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Image(painter = painterResource(id = helpRes), contentDescription = null)
        }
    }
}

@Preview
@Composable
private fun HelpDialogPreview() {
    KoinPreview {
        HelpDialog(android.R.drawable.ic_media_rew) {}
    }
}
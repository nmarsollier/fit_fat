package com.nmarsollier.fitfat.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.nmarsollier.fitfat.R
import com.nmarsollier.fitfat.utils.KoinPreview

/*
fun showHelpDialog(context: Context, helpRes: Int) {
    val bind = HelpDialogBinding.inflate(LayoutInflater.from(context), null, false)
    Dialog(context).apply {
        setContentView(bind.root)
        bind.vHelpView.setOnClickListener {
            dismiss()
        }
        bind.vHelpPicture.setImageResource(helpRes)
        show()
    }
}*/

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
fun HelpDialogPreview() {
    KoinPreview {
        HelpDialog(R.drawable.img_bicep) {}
    }
}
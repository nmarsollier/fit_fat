package com.nmarsollier.fitfat.components

import android.app.Dialog
import android.content.Context
import com.nmarsollier.fitfat.R
import kotlinx.android.synthetic.main.help_dialog.*

fun showHelpDialog(context: Context, resId: Int) {
    Dialog(context).apply {
        setContentView(R.layout.help_dialog)
        vHelpView.setOnClickListener {
            dismiss()
        }
        vHelpPicture.setImageResource(resId)
        show()
    }
}
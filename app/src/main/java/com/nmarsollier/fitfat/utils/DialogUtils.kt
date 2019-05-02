package com.nmarsollier.fitfat.utils

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import com.nmarsollier.fitfat.R

fun showProgressDialog(context: Context): Dialog {
    return AlertDialog.Builder(context)
        .setView(R.layout.progress)
        .create()
        .also {
            it.show()
        }
}
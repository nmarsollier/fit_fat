package com.nmarsollier.fitfat.utils

import android.app.Dialog
import android.content.Context
import android.view.Window
import androidx.appcompat.app.AppCompatDialog
import com.nmarsollier.fitfat.R

fun showProgressDialog(context: Context): Dialog {
    return AppCompatDialog(context).apply {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setCancelable(false)
        setContentView(R.layout.progress)
        show()
    }
}
package com.nmarsollier.fitfat.utils

import android.content.Context
import android.content.res.Resources
import android.graphics.PorterDuff
import android.view.Menu
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.forEach
import com.nmarsollier.fitfat.R
import java.util.logging.Level
import java.util.logging.Logger


fun updateMenuItemColor(menu: Menu?, resources: Resources) {
    menu?.forEach { menuItem ->
        val drawable = menuItem.icon
        if (drawable != null) {
            drawable.mutate()
            drawable.setColorFilter(resources.getColor(R.color.menuItemColor), PorterDuff.Mode.SRC_ATOP)
        }
    }
}

fun logInfo(msg: String) {
    Logger.getGlobal().log(Level.INFO, msg)
}

fun logError(msg: String) {
    Logger.getGlobal().log(Level.SEVERE, msg)
}

fun logError(err: Exception) {
    Logger.getGlobal().log(Level.SEVERE, err.toString())
}

fun AppCompatActivity.closeKeyboard() {
    this.currentFocus?.let { view ->
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
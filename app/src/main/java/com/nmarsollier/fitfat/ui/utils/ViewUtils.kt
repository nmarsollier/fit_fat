package com.nmarsollier.fitfat.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.view.Menu
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import com.nmarsollier.fitfat.R
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

val logger by logger()

fun Context.updateMenuItemColor(menu: Menu?) {
    menu?.forEach { menuItem ->
        val color = if (menuItem.isEnabled) R.color.menuItemColor else R.color.menuItemColorDisabled

        val drawable = menuItem.icon
        if (drawable != null) {
            drawable.mutate()
            drawable.setColorFilter(ContextCompat.getColor(this, color), PorterDuff.Mode.SRC_ATOP)
        }
    }
}

fun AppCompatActivity.closeKeyboard() {
    this.currentFocus?.let { view ->
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }
}

fun Context.dpToPx(dp: Number): Float {
    return dp.toFloat() * this.resources.displayMetrics.density
}

fun runInBackground(backgroundProcess: () -> Unit) {
    GlobalScope.launch {
        backgroundProcess.invoke()
    }
}

fun Activity.runInForeground(foregroundProcess: () -> Unit) {
    MainScope().launch {
        applicationContext ?: return@launch
        try {
            foregroundProcess.invoke()
        } catch (exception: java.lang.IllegalStateException) {
            logger.severe("missing Activity.applicationContext in runInForeground $exception")
        }
    }
}

fun Fragment.runInForeground(foregroundProcess: () -> Unit) {
    MainScope().launch {
        context ?: return@launch
        try {
            foregroundProcess.invoke()
        } catch (exception: java.lang.IllegalStateException) {
            logger.severe("missing Fragment.context in runInForeground $exception")
        }
    }
}

fun runInForeground(foregroundProcess: () -> Unit) {
    MainScope().launch {
        try {
            foregroundProcess.invoke()
        } catch (exception: java.lang.IllegalStateException) {
            logger.severe("missing context in runInForeground $exception")
        }
    }
}

fun Fragment.openDbInspector() {
    try {
        val intent = Intent()
        intent.setClassName(
            requireActivity().packageName,
            "im.dino.dbinspector.activities.DbInspectorActivity"
        )
        startActivity(intent)
    } catch (e: Exception) {
        logger.severe("Unable to launch db inspector $e")
    }
}

fun Context.showToast(text: Int) {
    Toast.makeText(
        applicationContext,
        text,
        Toast.LENGTH_LONG
    ).show()
}
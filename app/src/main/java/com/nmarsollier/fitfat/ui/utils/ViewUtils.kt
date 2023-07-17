package com.nmarsollier.fitfat.utils

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.util.Log
import android.view.Menu
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import com.nmarsollier.fitfat.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*

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

fun Any.logInfo(msg: String) {
    Log.i(this.javaClass.name, msg)
}

fun Any.logError(msg: String, e: Exception? = null) {
    Log.e(this.javaClass.name, msg, e)
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
            logError("missing Activity.applicationContext in runInForeground", exception)
        }
    }
}

fun Fragment.runInForeground(foregroundProcess: () -> Unit) {
    MainScope().launch {
        context ?: return@launch
        try {
            foregroundProcess.invoke()
        } catch (exception: java.lang.IllegalStateException) {
            logError("missing Fragment.context in runInForeground", exception)
        }
    }
}

fun runInForeground(foregroundProcess: () -> Unit) {
    MainScope().launch {
        try {
            foregroundProcess.invoke()
        } catch (exception: java.lang.IllegalStateException) {
            logError("missing context in runInForeground", exception)
        }
    }
}

fun Fragment.showDatePicker(date: Date, onChange: (date: Date) -> Unit) {
    val birthCalendar = Calendar.getInstance()
    birthCalendar.time = date

    val datePickerDialog = DatePickerDialog(
        requireContext(),
        { _, year, monthOfYear, dayOfMonth ->
            val newDate = Calendar.getInstance()
            newDate.set(year, monthOfYear, dayOfMonth)
            onChange.invoke(newDate.time)
        },
        birthCalendar.get(Calendar.YEAR),
        birthCalendar.get(Calendar.MONTH),
        birthCalendar.get(Calendar.DAY_OF_MONTH)
    )
    datePickerDialog.show()
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
        logError("Unable to launch db inspector", e)
    }
}

fun <T> StateFlow<T>.observe(scope: CoroutineScope, collector: FlowCollector<T>) = scope.launch {
    collect(collector)
}

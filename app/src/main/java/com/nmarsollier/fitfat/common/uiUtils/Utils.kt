package com.nmarsollier.fitfat.common.uiUtils

import android.app.DatePickerDialog
import android.content.Context
import android.graphics.PorterDuff
import android.view.Menu
import android.view.Window
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialog
import androidx.core.content.ContextCompat
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import com.nmarsollier.fitfat.R
import java.util.Calendar
import java.util.Date


fun AppCompatActivity.closeKeyboard() {
    this.currentFocus?.let { view ->
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }
}


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


private var dialog: AppCompatDialog? = null

fun Fragment.showProgressDialog() {
    AppCompatDialog(this.requireContext()).apply {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setCancelable(false)
        setContentView(R.layout.progress)
        show()
        dialog = this
    }
}

fun Fragment.closeProgressDialog() {
    dialog?.let {
        dialog = null
        it.dismiss()
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
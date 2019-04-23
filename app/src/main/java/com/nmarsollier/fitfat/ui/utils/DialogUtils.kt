package com.nmarsollier.fitfat.ui.utils

import android.app.DatePickerDialog
import android.view.Window
import androidx.appcompat.app.AppCompatDialog
import androidx.fragment.app.Fragment
import com.nmarsollier.fitfat.R
import java.util.*

private var dialog: AppCompatDialog? = null

fun Fragment.showProgressDialog() {
    AppCompatDialog(this.requireContext()).apply {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setCancelable(false)
        setContentView(R.layout.progress)
        show()
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

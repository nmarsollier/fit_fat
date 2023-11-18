package com.nmarsollier.fitfat.utils.ui.dialogs

import android.app.DatePickerDialog
import android.content.Context
import java.util.Calendar
import java.util.Date

fun showDatePicker(context: Context, date: Date, onChange: (date: Date) -> Unit) {
    val birthCalendar = Calendar.getInstance()
    birthCalendar.time = date

    val datePickerDialog = DatePickerDialog(
        context,
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

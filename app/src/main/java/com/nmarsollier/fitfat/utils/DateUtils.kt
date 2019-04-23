package com.nmarsollier.fitfat.utils

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

val UTC_FORMAT = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
    timeZone = TimeZone.getTimeZone("UTC")
}

val APP_FORMAT_DATE = DateFormat.getDateInstance(DateFormat.DEFAULT, Locale.getDefault())
val APP_FORMAT_DATETIME = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT, Locale.getDefault())

/**
 * Extension on Date to formatDate Iso 8601
 */
fun Date.toIso8601() = UTC_FORMAT.format(this)

/**
 * Extension on String to parse as Iso 8601 Date
 */
fun String.parseIso8601() = UTC_FORMAT.parse(this)


/**
 * Extension on Date to formatDate
 */
fun Date.formatDate() = APP_FORMAT_DATE.format(this)

fun Date.formatDateTime() = APP_FORMAT_DATETIME.format(this)

/**
 * Extension on String to parse Date
 */
fun String.parseDate() = APP_FORMAT_DATE.parse(this)

fun Date.getAge(): Int {
    val dob = Calendar.getInstance()
    dob.time = this
    val today = Calendar.getInstance()

    var age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR)
    if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
        age--
    }
    return age
}
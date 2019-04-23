package com.nmarsollier.fitfat.utils

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

val APP_FORMAT_SHORT: DateFormat = DateFormat.getDateInstance(DateFormat.SHORT)
val APP_FORMAT_DATE: DateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT)
val APP_FORMAT_DATETIME: DateFormat =
    DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT)

val UTC_FORMAT = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
    timeZone = TimeZone.getTimeZone("UTC")
}

/**
 * Extension on Date to formatDate Iso 8601
 */
fun Date.toIso8601() = UTC_FORMAT.format(this)

/**
 * Extension on String to parse as Iso 8601 Date
 */
fun String.parseIso8601() = UTC_FORMAT.parse(this)


fun Date.truncateTime(): Long {
    return Calendar.getInstance().also {
        it.time = this
        it.set(Calendar.HOUR_OF_DAY, 0)
        it.set(Calendar.MINUTE, 0)
        it.set(Calendar.SECOND, 0)
        it.set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}

/**
 * Extension on Date to formatDate
 */
fun Date.formatDate(): String = APP_FORMAT_DATE.format(this)

fun Date.formatShortDate(): String = APP_FORMAT_SHORT.format(this)

fun Date.formatDateTime(): String = APP_FORMAT_DATETIME.format(this)

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

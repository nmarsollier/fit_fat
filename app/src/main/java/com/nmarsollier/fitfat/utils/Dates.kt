package com.nmarsollier.fitfat.utils

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

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
inline val Date?.toIso8601
    get() = this?.let { UTC_FORMAT.format(it) }

/**
 * Extension on String to parse as Iso 8601 Date
 */
inline val String?.parseIso8601
    get() = this?.let { UTC_FORMAT.parse(it) }

inline val Date.truncateTime
    get(): Long {
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
inline val Date.formatDate
    get(): String = APP_FORMAT_DATE.format(this)

inline val Date.formatShortDate
    get(): String = APP_FORMAT_SHORT.format(this)

inline val Date.formatDateTime
    get(): String = APP_FORMAT_DATETIME.format(this)

fun dateOf(year: Int, month: Int, day: Int): Date = Date.from(
    ZonedDateTime.of(
        year, month, day,
        0, 0, 0, 0,
        ZoneId.systemDefault()
    ).toInstant()
)
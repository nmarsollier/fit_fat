package com.nmarsollier.fitfat.utils

import java.text.NumberFormat

inline val Double.toPounds
    get() = this * 2.20462

inline val Double.toKg
    get() = this * 0.453592

inline val Double.toCm
    get() = this * 2.54

inline val Double.toInch
    get() = this * 0.393701

fun Double.formatString(default: String = ""): String {
    return try {
        String.format("%.2f", this)
    } catch (e: Exception) {
        default
    }
}

fun Int.formatString(default: String = ""): String {
    return try {
        NumberFormat.getInstance().format(this)
    } catch (e: Exception) {
        default
    }
}

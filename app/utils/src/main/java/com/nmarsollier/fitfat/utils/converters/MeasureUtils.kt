package com.nmarsollier.fitfat.utils.converters

import java.text.NumberFormat

fun Double.toPounds() = this * 2.20462

fun Double.toKg() = this * 0.453592

fun Double.toCm() = this * 2.54

fun Double.toInch() = this * 0.393701

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

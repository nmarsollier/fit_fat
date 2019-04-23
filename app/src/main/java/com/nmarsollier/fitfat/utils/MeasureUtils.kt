package com.nmarsollier.fitfat.utils

import com.nmarsollier.fitfat.model.MeasureType
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

fun String.parseDouble(default: Double = 0.0): Double {
    return try {
        NumberFormat.getInstance().parse(this).toDouble()
    } catch (e: Exception) {
        default
    }
}

fun Double.toStdWeight(type: MeasureType): Double {
    return if (type == MeasureType.IMPERIAL) {
        this.toKg()
    } else {
        this
    }
}

fun Double.toStdWidth(type: MeasureType): Double {
    return if (type == MeasureType.IMPERIAL) {
        this.toCm()
    } else {
        this
    }
}

fun CharSequence?.toStdWidth(type: MeasureType): Double {
    return this?.toString()?.parseDouble()?.toStdWidth(type) ?: 0.0
}

fun CharSequence?.toStdWeight(type: MeasureType): Double {
    return this?.toString()?.parseDouble()?.toStdWeight(type) ?: 0.0
}
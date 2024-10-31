package com.nmarsollier.fitfat.utils

fun String?.nullIfEmpty(): String? {
    if (this.isNullOrEmpty()) return null
    return this
}

@SinceKotlin("1.1")
fun String.toDoubleOr(default: Double): Double = this.toDoubleOrNull() ?: default

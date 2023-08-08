package com.nmarsollier.fitfat.utils

fun String?.nullIfEmpty(): String? {
    if (this.isNullOrEmpty()) return null
    return this
}

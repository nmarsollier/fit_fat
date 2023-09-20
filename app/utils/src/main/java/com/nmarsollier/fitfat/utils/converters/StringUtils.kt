package com.nmarsollier.fitfat.utils.converters

fun String?.nullIfEmpty(): String? {
    if (this.isNullOrEmpty()) return null
    return this
}

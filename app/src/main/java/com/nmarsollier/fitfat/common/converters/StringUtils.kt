package com.nmarsollier.fitfat.common.converters

fun String?.nullIfEmpty(): String? {
    if (this.isNullOrEmpty()) return null
    return this
}

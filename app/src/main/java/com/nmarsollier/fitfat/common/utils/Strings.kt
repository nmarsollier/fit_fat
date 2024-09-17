package com.nmarsollier.fitfat.common.utils

fun String?.nullIfEmpty(): String? {
    if (this.isNullOrEmpty()) return null
    return this
}

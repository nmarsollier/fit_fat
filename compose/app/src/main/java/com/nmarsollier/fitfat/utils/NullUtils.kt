package com.nmarsollier.fitfat.utils

fun <A, B, R> ifNotNull(a: A?, b: B?, code: (A, B) -> R): Unit? {
    return if (a != null && b != null) {
        code.invoke(a, b)
        Unit
    } else {
        null
    }
}

fun String?.nullIfEmpty(): String? {
    if (this.isNullOrEmpty()) return null
    return this
}

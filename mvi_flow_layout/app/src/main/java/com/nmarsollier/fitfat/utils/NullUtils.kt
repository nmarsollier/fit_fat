package com.nmarsollier.fitfat.utils

fun <A, B, R> ifNotNull(a: A?, b: B?, code: (A, B) -> R): Unit? {
    return if (a != null && b != null) {
        code.invoke(a, b)
        Unit
    } else {
        null
    }
}

fun <A, B, C, R> ifNotNull(a: A?, b: B?, c: C?, code: (A, B, C) -> R): Unit? {
    return if (a != null && b != null && c != null) {
        code.invoke(a, b, c)
        Unit
    } else {
        null
    }
}

fun <A, B, C, D, R> ifNotNull(a: A?, b: B?, c: C?, d: D?, code: (A, B, C, D) -> R): Unit? {
    return if (a != null && b != null && c != null && d != null) {
        code.invoke(a, b, c, d)
        Unit
    } else {
        null
    }
}

fun String?.nullIfEmpty(): String? {
    if (this.isNullOrEmpty()) return null
    return this
}

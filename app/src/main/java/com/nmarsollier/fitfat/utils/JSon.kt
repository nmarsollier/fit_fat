package com.nmarsollier.fitfat.utils

import kotlinx.serialization.*
import kotlinx.serialization.json.*

val json = Json {
    ignoreUnknownKeys = true
}

inline fun <reified T> String?.jsonToObject(): T? {
    if (this == null) {
        return null
    }
    return try {
        json.decodeFromString<T>(this)
    } catch (e: Exception) {
        null
    }
}

inline fun <reified T> T?.toJson(): String? {
    this ?: return null
    return json.encodeToString(this)
}
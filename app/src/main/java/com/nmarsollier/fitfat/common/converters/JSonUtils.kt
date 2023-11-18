package com.nmarsollier.fitfat.common.converters

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken

inline fun <reified T> String?.jsonToObject(): T? {
    if (this == null) {
        return null
    }
    return try {
        val gson = GsonBuilder()
            .create()
        gson.fromJson(this, object : TypeToken<T>() {}.type)
    } catch (e: JsonSyntaxException) {
        null
    }
}

fun Any.toJson(): String {
    return Gson().toJson(this)
}
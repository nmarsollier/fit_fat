package com.nmarsollier.fitfat.common.converters

import com.google.gson.annotations.SerializedName

/**
 * Convert enum string constant into Typed Enum,
 * First checks the enum string, if none matches, then check
 * for @SerializedName
 *
 * or null if not possible.
 *
 * Usage: "STRVALUE".deserialize<EnumType>()
 */
inline fun <reified T : Enum<T>> String?.deserialize(): T? {
    if (this == null) {
        return null
    }
    return enumValues<T>().firstOrNull { it.name == this }
        ?: enumValues<T>().firstOrNull { it.serializedName == this }
}

/**
 * Returns the value of  @SerializedName
 * If not defined @SerializedName returns the enum string
 */
val Enum<*>?.serializedName: String?
    get() {
        return this?.javaClass?.getField(this.name)?.getAnnotation(SerializedName::class.java)?.value
            ?: this?.toString()
    }

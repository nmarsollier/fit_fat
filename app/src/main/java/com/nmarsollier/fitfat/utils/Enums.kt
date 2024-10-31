package com.nmarsollier.fitfat.utils

import kotlinx.serialization.*

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
        return this?.javaClass?.getField(this.name)
            ?.getAnnotation(SerialName::class.java)?.value
            ?: this?.toString()
    }

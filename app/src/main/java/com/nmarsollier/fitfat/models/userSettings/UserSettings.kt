package com.nmarsollier.fitfat.models.userSettings

import androidx.compose.runtime.*
import com.nmarsollier.fitfat.models.userSettings.db.*
import com.nmarsollier.fitfat.utils.*
import java.util.*

@Stable
@Immutable
data class UserSettings(
    val uid: Int,
    val displayName: String = "",
    val birthDate: Date = Date(),
    val weight: Double = 0.0,
    val height: Double = 0.0,
    val sex: SexType = SexType.MALE,
    val measureSystem: MeasureType = MeasureType.METRIC,
    val firebaseToken: String? = null
) {
    fun isNew() = weight == 0.0 || height == 0.0

    companion object
}

internal val UserSettingsEntity.asUserSettings
    get() = UserSettings(
        uid = uid,
        displayName = displayName,
        birthDate = birthDate,
        weight = weight,
        height = height,
        sex = sex,
        measureSystem = measureSystem,
        firebaseToken = firebaseToken
    )


fun UserSettings.updateFirebaseToken(token: String?): UserSettings {
    return copy(firebaseToken = token)
}

fun UserSettings.updateWeight(bodyWeight: Double?): UserSettings {
    return bodyWeight?.let {
        val value =
            if (measureSystem == MeasureType.IMPERIAL) it.toKg
            else it

        copy(weight = value)
    } ?: this
}

fun UserSettings.updateBirthDate(newBirthDate: Date?): UserSettings {
    return newBirthDate?.let { copy(birthDate = newBirthDate) } ?: this
}

fun UserSettings.updateDisplayName(newName: String?): UserSettings {
    return newName?.let { copy(displayName = it) } ?: this
}

fun UserSettings.updateHeight(newHeight: Double?): UserSettings {
    return newHeight?.let {
        val value =
            if (measureSystem == MeasureType.IMPERIAL) it.toCm
            else it
        copy(height = value)
    } ?: this
}

fun UserSettings.updateMeasureSystem(system: MeasureType?): UserSettings =
    system?.let { copy(measureSystem = it) } ?: this

fun UserSettings.updateSex(newSex: SexType?): UserSettings =
    newSex?.let { copy(sex = it) } ?: this


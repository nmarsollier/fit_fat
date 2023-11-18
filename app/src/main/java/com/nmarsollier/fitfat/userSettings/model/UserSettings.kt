package com.nmarsollier.fitfat.userSettings.model

import com.nmarsollier.fitfat.userSettings.model.db.UserSettingsData
import com.nmarsollier.fitfat.common.converters.toCm
import com.nmarsollier.fitfat.common.converters.toKg
import java.util.Date

data class UserSettings internal constructor(
    private var rootEntity: UserSettingsData
) {
    val value
        get() = rootEntity

    fun updateFirebaseToken(token: String?) {
        rootEntity = rootEntity.copy(firebaseToken = token)
    }

    fun updateWeight(bodyWeight: Double?) {
        bodyWeight?.let {
            val value =
                if (rootEntity.measureSystem == UserSettingsData.MeasureType.IMPERIAL) it.toKg()
                else it

            rootEntity = rootEntity.copy(weight = value)
        }
    }

    fun updateBirthDate(newBirthDate: Date?) {
        newBirthDate?.let { rootEntity = rootEntity.copy(birthDate = newBirthDate) }
    }

    fun updateDisplayName(newName: String?) {
        newName?.let { rootEntity = rootEntity.copy(displayName = it) }
    }

    fun updateHeight(newHeight: Double?) {
        newHeight?.let {
            val value =
                if (rootEntity.measureSystem == UserSettingsData.MeasureType.IMPERIAL) it.toCm()
                else it
            rootEntity = rootEntity.copy(height = value)
        }
    }

    fun updateMeasureSystem(system: UserSettingsData.MeasureType?) =
        system?.let { rootEntity = rootEntity.copy(measureSystem = it) }

    fun updateSex(newSex: UserSettingsData.SexType?) {
        newSex?.let { rootEntity = rootEntity.copy(sex = it) }
    }

    companion object
}

internal val UserSettingsData.asUserSettings
    get() = UserSettings(this)

package com.nmarsollier.fitfat.userSettings.model

import com.nmarsollier.fitfat.userSettings.model.db.UserSettingsData
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
        bodyWeight?.let { rootEntity = rootEntity.copy(weight = it) }
    }

    fun updateBirthDate(newBirthDate: Date?) {
        newBirthDate?.let { rootEntity = rootEntity.copy(birthDate = newBirthDate) }
    }

    fun updateDisplayName(newName: String?) {
        newName?.let { rootEntity = rootEntity.copy(displayName = it) }
    }

    fun updateHeight(newHeight: Double?) {
        newHeight?.let { rootEntity = rootEntity.copy(height = it) }
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

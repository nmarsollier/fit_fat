package com.nmarsollier.fitfat.models.userSettings

import com.nmarsollier.fitfat.common.utils.RepositoryUpdate
import com.nmarsollier.fitfat.common.utils.StateRepository
import com.nmarsollier.fitfat.models.userSettings.db.UserSettingsDao
import com.nmarsollier.fitfat.models.userSettings.db.UserSettingsEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class UserSettingsRepository internal constructor(
    private val dao: UserSettingsDao
) : StateRepository() {
    suspend fun findCurrent(): UserSettings = coroutineScope {
        val us = dao.findCurrent() ?: UserSettingsEntity(1).also {
            dao.update(it)
        }

        us.asUserSettings
    }

    suspend fun update(userSettings: UserSettings?) = coroutineScope {
        userSettings?.let {
            launch(Dispatchers.IO) {
                dao.update(it.asEntity)
            }.join()
            RepositoryUpdate.Save.sendToEvent()
        }
    }
}

val UserSettings.asEntity
    get() = UserSettingsEntity(
        uid = uid,
        displayName = displayName,
        birthDate = birthDate,
        weight = weight,
        height = height,
        sex = sex,
        measureSystem = measureSystem,
        firebaseToken = firebaseToken
    )
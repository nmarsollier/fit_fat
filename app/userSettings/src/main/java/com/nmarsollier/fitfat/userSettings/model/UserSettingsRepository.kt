package com.nmarsollier.fitfat.userSettings.model

import com.nmarsollier.fitfat.userSettings.model.db.UserSettingsDao
import com.nmarsollier.fitfat.userSettings.model.db.UserSettingsData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class UserSettingsRepository internal constructor(
    private val dao: UserSettingsDao
) {
    suspend fun findCurrent(): UserSettings = coroutineScope {
        val us = dao.findCurrent() ?: UserSettingsData(1).also {
            launch(Dispatchers.IO) {
                dao.update(it)
            }
        }

        us.asUserSettings
    }

    suspend fun update(userSettingsEntity: UserSettings?) = coroutineScope {
        userSettingsEntity?.let {
            dao.update(it.value)
        }
    }
}

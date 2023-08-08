package com.nmarsollier.fitfat.model.userSettings

import com.nmarsollier.fitfat.model.db.FitFatDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class UserSettingsRepository(
    private val database: FitFatDatabase
) {
    suspend fun load(): UserSettingsEntity = coroutineScope {
        val dao = database.userDao()

        dao.findCurrent() ?: UserSettingsEntity(1).also {
            launch(Dispatchers.IO) {
                dao.insert(it)
            }
        }
    }

    suspend fun update(userSettingsEntity: UserSettingsEntity) = coroutineScope {
        database.userDao().update(userSettingsEntity)
    }

    suspend fun updateFirebaseToken(token: String?) {
        val dao = database.userDao()
        val userSettings = load()
        userSettings.firebaseToken = token
        dao.update(userSettings)
    }
}

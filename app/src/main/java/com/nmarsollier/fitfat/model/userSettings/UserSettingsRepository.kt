package com.nmarsollier.fitfat.model.userSettings

import com.nmarsollier.fitfat.model.db.FitFatDatabase
import com.nmarsollier.fitfat.model.firebase.UserSettingsDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserSettingsRepository(
    private val database: FitFatDatabase
) {
    suspend fun load(): UserSettings = coroutineScope {
        val dao = database.userDao()

        dao.findCurrent() ?: UserSettings(1).also {
            dao.insert(it)
        }
    }

    fun update(userSettings: UserSettings) = MainScope().launch(Dispatchers.IO) {
        database.userDao().update(userSettings)
    }

    fun updateFirebaseToken(token: String?) = MainScope().launch(Dispatchers.IO) {
        val dao = database.userDao()
        load().let { userSettings ->
            userSettings.firebaseToken = token
            dao.update(userSettings)
        }
    }

    fun updateFromFirebase(
        document: UserSettingsDto?
    ) = MainScope().launch(Dispatchers.IO) {
        if (document == null) {
            return@launch
        }

        val dao = database.userDao()
        withContext(Dispatchers.IO) {
            load().let { userSettings ->
                document.birthDate?.let {
                    userSettings.birthDate = it
                }

                document.displayName?.let {
                    userSettings.displayName = it
                }

                document.height?.let {
                    userSettings.height = it
                }

                document.weight?.let {
                    userSettings.weight = it
                }

                document.measureSystem?.let {
                    userSettings.measureSystem = it
                }

                document.sex?.let {
                    userSettings.sex = it
                }

                dao.update(userSettings)
            }
        }
    }
}


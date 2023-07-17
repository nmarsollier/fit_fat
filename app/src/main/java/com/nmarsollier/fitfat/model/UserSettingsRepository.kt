package com.nmarsollier.fitfat.model

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

object UserSettingsRepository {
    fun load(context: Context): Flow<UserSettings> = channelFlow {
        val dao = getRoomDatabase(context).userDao()
        withContext(Dispatchers.IO) {
            dao.findCurrent().firstOrNull {
                if (it == null) {
                    UserSettings(1).also { userSettings ->
                        dao.insert(userSettings)
                        send(userSettings)
                    }
                } else {
                    send(it)
                }
                true
            }
        }
    }

    fun save(context: Context, userSettings: UserSettings): Flow<UserSettings> {
        runBlocking(Dispatchers.IO) {
            getRoomDatabase(context).userDao().update(userSettings)
        }
        //FirebaseDao.uploadUserSettings(userSettings)
        return load(context)
    }
}

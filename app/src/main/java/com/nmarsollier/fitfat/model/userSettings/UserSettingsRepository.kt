package com.nmarsollier.fitfat.model.userSettings

import android.content.Context
import com.google.firebase.firestore.DocumentSnapshot
import com.nmarsollier.fitfat.model.db.getRoomDatabase
import com.nmarsollier.fitfat.model.firebase.FirebaseRepository
import com.nmarsollier.fitfat.utils.parseIso8601
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object UserSettingsRepository {
    fun load(context: Context): Flow<UserSettings> = channelFlow {
        val dao = getRoomDatabase(context).userDao()
        withContext(Dispatchers.IO) {
            dao.findCurrent().collect {
                if (it == null) {
                    UserSettings(1).also { userSettings ->
                        dao.insert(userSettings)
                        send(userSettings)
                        close()
                    }
                } else {
                    send(it)
                    close()
                }
            }
        }
        awaitClose()
    }

    fun save(context: Context, userSettings: UserSettings): Flow<UserSettings> = channelFlow {
        withContext(Dispatchers.IO) {
            getRoomDatabase(context).userDao().update(userSettings)
        }
        FirebaseRepository.uploadPendingMeasures(context)
        send(userSettings)
    }

    fun updateFirebaseToken(context: Context, token: String): Flow<UserSettings?> = channelFlow {
        val dao = getRoomDatabase(context).userDao()
        withContext(Dispatchers.IO) {
            dao.findCurrent().collect {
                (it ?: UserSettings(1).also { userSettings ->
                    dao.insert(userSettings)
                }).let { userSettings ->
                    userSettings.firebaseToken = token

                    dao.update(userSettings)
                    send(userSettings)
                    close()
                }
            }
        }
        awaitClose()
    }

    fun updateFirebaseData(
        context: Context,
        document: DocumentSnapshot?
    ) = GlobalScope.launch(Dispatchers.IO) {
        if (document == null) {
            return@launch
        }

        val dao = getRoomDatabase(context).userDao()
        withContext(Dispatchers.IO) {
            dao.findCurrent().collect {
                (it ?: UserSettings(1).also { userSettings ->
                    dao.insert(userSettings)
                }).let { userSettings ->
                    userSettings.birthDate =
                        document.getString("birthDate")?.parseIso8601()
                            ?: userSettings.birthDate
                    userSettings.displayName =
                        document.getString("displayName") ?: userSettings.displayName
                    userSettings.height =
                        document.getDouble("height") ?: userSettings.height
                    userSettings.weight =
                        document.getDouble("weight") ?: userSettings.weight
                    userSettings.measureSystem = MeasureType.valueOf(
                        document.getString("measureSystem")
                            ?: userSettings.measureSystem.toString()
                    )
                    userSettings.sex = SexType.valueOf(
                        document.getString("sex") ?: userSettings.sex.toString()
                    )

                    dao.update(userSettings)
                }
            }
        }
    }
}

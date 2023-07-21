package com.nmarsollier.fitfat.model.userSettings

import android.content.Context
import com.google.firebase.firestore.DocumentSnapshot
import com.nmarsollier.fitfat.model.db.getRoomDatabase
import com.nmarsollier.fitfat.model.firebase.FirebaseRepository
import com.nmarsollier.fitfat.utils.nullIfEmpty
import com.nmarsollier.fitfat.utils.parseIso8601
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
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
                    }
                } else {
                    send(it)
                }
            }
        }
    }

    fun save(context: Context, userSettings: UserSettings): Flow<UserSettings> = channelFlow {
        withContext(Dispatchers.IO) {
            getRoomDatabase(context).userDao().update(userSettings)
        }
        FirebaseRepository.uploadUserSettings(userSettings)
        send(userSettings)
        close()
    }

    fun updateFirebaseToken(context: Context, token: String?) = GlobalScope.launch(Dispatchers.IO) {
        val dao = getRoomDatabase(context).userDao()
        load(context).collect { userSettings ->
            userSettings.firebaseToken = token
            dao.update(userSettings)
        }
    }

    fun updateFromFirebase(
        context: Context,
        document: DocumentSnapshot?
    ) = GlobalScope.launch(Dispatchers.IO) {
        if (document == null) {
            return@launch
        }

        val dao = getRoomDatabase(context).userDao()
        withContext(Dispatchers.IO) {
            load(context).collect { userSettings ->
                document.getString("birthDate").nullIfEmpty()?.parseIso8601()?.let {
                    userSettings.birthDate = it
                }

                document.getString("displayName").nullIfEmpty()?.let {
                    userSettings.displayName = it
                }

                document.getDouble("height")?.takeIf { it > 0.0 }?.let {
                    userSettings.height = it
                }

                document.getDouble("weight")?.takeIf { it > 0.0 }?.let {
                    userSettings.weight = it
                }

                document.getString("measureSystem")?.nullIfEmpty()?.let {
                    userSettings.measureSystem = MeasureType.valueOf(it)
                }

                document.getString("sex")?.nullIfEmpty()?.let {
                    userSettings.sex = SexType.valueOf(it)
                }

                dao.update(userSettings)
            }
        }
    }
}

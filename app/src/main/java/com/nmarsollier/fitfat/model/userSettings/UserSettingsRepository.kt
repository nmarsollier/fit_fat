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
import kotlinx.coroutines.flow.take
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
    }

    fun updateFirebaseToken(context: Context, token: String?) = GlobalScope.launch(Dispatchers.IO) {
        val dao = getRoomDatabase(context).userDao()
        load(context).take(1).collect { userSettings ->
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
            load(context).take(1).collect { userSettings ->
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

private val DocumentSnapshot.birthDate
    get() = this.getString("birthDate").nullIfEmpty()?.parseIso8601()

private val DocumentSnapshot.displayName
    get() = this.getString("displayName").nullIfEmpty()

private val DocumentSnapshot.height
    get() = this.getDouble("height")?.takeIf { it > 0.0 }

private val DocumentSnapshot.weight
    get() = this.getDouble("weight")?.takeIf { it > 0.0 }

private val DocumentSnapshot.measureSystem
    get() = this.getString("measureSystem").nullIfEmpty()?.let {
        MeasureType.valueOf(it)
    }

private val DocumentSnapshot.sex
    get() = this.getString("sex").nullIfEmpty()?.let {
        SexType.valueOf(it)
    }

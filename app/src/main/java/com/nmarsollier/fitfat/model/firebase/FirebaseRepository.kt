package com.nmarsollier.fitfat.model.firebase

import android.content.Context
import com.nmarsollier.fitfat.model.measures.MeasuresRepository
import com.nmarsollier.fitfat.model.userSettings.UserSettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch

object FirebaseRepository {
    fun init(context: Context): Flow<Boolean?> = channelFlow {
        FirebaseDao.init(context).let {
            UserSettingsRepository.load(context).collect {
                it.firebaseToken?.let { token ->
                    FirebaseDao.googleAuth(token).collect { status ->
                        send(status)
                        close()
                    }
                } ?: close()
            }
        }
        awaitClose { }
    }

    fun googleAuth(token: String): Flow<Boolean?> = FirebaseDao.googleAuth(token)

    fun downloadUserSettings(
        context: Context,
        token: String
    ) = GlobalScope.launch(Dispatchers.IO) {
        FirebaseDao.downloadUserSettings().collect {
            it?.let {
                UserSettingsRepository.updateFirebaseData(context, it)
            }
        }
    }

    fun downloadMeasurements(
        context: Context
    ) = GlobalScope.launch(Dispatchers.IO) {
        UserSettingsRepository.load(context).collect {
            it.let { userSettings ->
                FirebaseDao.downloadMeasurements(context).collect { data ->
                    data?.forEach { document ->
                        MeasuresRepository.updateFirebaseData(context, userSettings, document)
                    }
                }
            }
        }

        uploadPendingMeasures(context)
    }

    fun uploadPendingMeasures(context: Context) {
        FirebaseDao.uploadPendingMeasures(context)
    }
}
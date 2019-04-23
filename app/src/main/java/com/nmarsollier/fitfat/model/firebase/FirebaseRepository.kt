package com.nmarsollier.fitfat.model.firebase

import android.content.Context
import com.nmarsollier.fitfat.model.measures.Measure
import com.nmarsollier.fitfat.model.measures.MeasuresRepository
import com.nmarsollier.fitfat.model.userSettings.UserSettings
import com.nmarsollier.fitfat.model.userSettings.UserSettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch

object FirebaseRepository {
    fun init(context: Context): Flow<Boolean?> = channelFlow {
        FirebaseDao.init().let {
            UserSettingsRepository.load(context).take(1).collect {
                it.firebaseToken?.let { token ->
                    FirebaseDao.googleAuth(token).collect { status ->
                        send(status)
                        close()
                    }
                } ?: close()
            }
        }
        awaitClose()
    }

    fun googleAuth(token: String): Flow<Boolean?> = FirebaseDao.googleAuth(token)

    fun downloadUserSettings(
        context: Context,
        token: String
    ) = GlobalScope.launch(Dispatchers.IO) {
        FirebaseDao.downloadUserSettings().take(1).collect {
            it?.let {
                UserSettingsRepository.updateFromFirebase(context, it)
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
                        MeasuresRepository.updateFromFirebase(context, userSettings, document)
                    }
                }
            }
        }

        uploadPendingMeasures(context)
    }

    fun uploadPendingMeasures(context: Context) {
        FirebaseDao.uploadPendingMeasures(context)
    }

    fun uploadUserSettings(userSettings: UserSettings) {
        FirebaseDao.uploadUserSettings(userSettings)
    }

    fun deleteMeasure(measure: Measure) {
        FirebaseDao.deleteMeasure(measure.uid)
    }
}
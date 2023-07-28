package com.nmarsollier.fitfat.model.firebase

import com.nmarsollier.fitfat.model.measures.Measure
import com.nmarsollier.fitfat.model.measures.MeasuresRepository
import com.nmarsollier.fitfat.model.userSettings.UserSettings
import com.nmarsollier.fitfat.model.userSettings.UserSettingsRepository
import com.nmarsollier.fitfat.utils.collectOnce
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch

object FirebaseRepository {
    fun init(): Flow<Boolean?> = channelFlow {
        FirebaseDao.init().let {
            UserSettingsRepository.load().collectOnce {
                it.firebaseToken?.let { token ->
                    FirebaseDao.signInWithGoogle(token).collectOnce { status ->
                        send(status)
                    }
                }
            }
        }
        awaitClose()
    }

    fun googleAuth(token: String): Flow<Boolean?> = FirebaseDao.signInWithGoogle(token)

    fun downloadUserSettings() = MainScope().launch(Dispatchers.IO) {
        FirebaseDao.downloadUserSettings().collectOnce {
            it?.let {
                UserSettingsRepository.updateFromFirebase(it)
            }
        }
    }

    fun downloadMeasurements() = MainScope().launch(Dispatchers.IO) {
        UserSettingsRepository.load().collect {
            it.let { userSettings ->
                FirebaseDao.downloadMeasurements().collect { data ->
                    data?.forEach { document ->
                        MeasuresRepository.updateFromFirebase(
                            userSettings, document
                        )
                    }
                }
            }
        }

        uploadPendingMeasures()
    }

    fun uploadPendingMeasures() {
        FirebaseDao.uploadPendingMeasures()
    }

    fun uploadUserSettings(userSettings: UserSettings) {
        FirebaseDao.uploadUserSettings(userSettings)
    }

    fun deleteMeasure(measure: Measure) {
        FirebaseDao.deleteMeasure(measure.uid)
    }
}
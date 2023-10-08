package com.nmarsollier.fitfat.useCases

import com.nmarsollier.fitfat.model.firebase.MeasuresFirebaseRepository
import com.nmarsollier.fitfat.model.measures.MeasuresRepository
import com.nmarsollier.fitfat.model.userSettings.UserSettingsRepository
import com.nmarsollier.fitfat.utils.logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class FirebaseMeasuresSyncUseCase(
    private val measuresFirebaseRepository: MeasuresFirebaseRepository,
    private val userSettingsRepository: UserSettingsRepository,
    private val measuresRepository: MeasuresRepository
) {
    fun downloadAndSyncMeasures() = MainScope().launch(Dispatchers.IO) {
        userSettingsRepository.load().let { userSettings ->
            measuresFirebaseRepository.loadAll(userSettings).let { measures ->
                measures?.forEach { measure ->
                    measuresRepository.insert(measure)
                }
            }
        }
    }

    fun uploadPendingMeasures() = MainScope().launch(Dispatchers.IO) {
        val measures = measuresRepository.findUnSynced() ?: return@launch

        measuresFirebaseRepository.update(measures)?.let { status ->
            if (status.isSuccessful) {
                measures.forEach { measure ->
                    measure.cloudSync = true
                    measuresRepository.update(measure)
                }
            } else {
                logger.severe("Error saving UserSettings ${status.exception}")
            }
        }
    }
}
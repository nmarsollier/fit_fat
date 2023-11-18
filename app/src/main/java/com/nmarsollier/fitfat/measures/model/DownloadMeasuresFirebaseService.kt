package com.nmarsollier.fitfat.measures.model

import com.nmarsollier.fitfat.common.firebase.FirebaseConnection
import com.nmarsollier.fitfat.measures.model.api.MeasuresFirebaseApi
import com.nmarsollier.fitfat.userSettings.model.UserSettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

internal class DownloadMeasuresFirebaseService(
    private val measuresRepository: com.nmarsollier.fitfat.measures.model.MeasuresRepository,
    private val measuresFirebaseApi: MeasuresFirebaseApi,
    private val userSettingsRepository: UserSettingsRepository,
    private val firebaseConnection: FirebaseConnection
) {
    init {
        MainScope().launch {
            firebaseConnection.statusFlow.collect {
                if (it) {
                    downloadAndSyncMeasures()
                }
            }
        }
    }

    private fun downloadAndSyncMeasures() = MainScope().launch(Dispatchers.IO) {
        measuresFirebaseApi
            .findAll(userSettingsRepository.findCurrent())
            ?.forEach {
                it.updateCloudSync(false)
                measuresRepository.update(it)
            }
    }
}
package com.nmarsollier.fitfat.models.measures

import com.nmarsollier.fitfat.models.firebase.FirebaseConnection
import com.nmarsollier.fitfat.models.measures.api.MeasuresFirebaseApi
import com.nmarsollier.fitfat.models.userSettings.UserSettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal class DownloadMeasuresFirebaseService(
    private val measuresRepository: MeasuresRepository,
    private val measuresFirebaseApi: MeasuresFirebaseApi,
    private val userSettingsRepository: UserSettingsRepository,
    private val firebaseConnection: FirebaseConnection
) {
    init {
        CoroutineScope(Dispatchers.IO).launch {
            firebaseConnection.statusFlow.collect {
                if (it) {
                    downloadAndSyncMeasures()
                }
            }
        }
    }

    private fun downloadAndSyncMeasures() = CoroutineScope(Dispatchers.IO).launch {
        measuresFirebaseApi
            .findAll(userSettingsRepository.findCurrent())
            ?.forEach {
                measuresRepository.update(it.updateCloudSync(false))
            }
    }
}
package com.nmarsollier.fitfat.models.userSettings

import com.nmarsollier.fitfat.models.firebase.FirebaseConnection
import com.nmarsollier.fitfat.models.userSettings.api.UserSettingsFirebaseApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal class DownloadSyncFirebaseService(
    private val userSettingsFirebaseApi: UserSettingsFirebaseApi,
    private val userSettingsRepository: UserSettingsRepository,
    private val firebaseConnection: FirebaseConnection
) {
    init {
        CoroutineScope(Dispatchers.IO).launch {
            firebaseConnection.statusFlow.collect {
                if (it) {
                    downloadAndSyncUserSettings()
                }
            }
        }
    }

    private fun downloadAndSyncUserSettings() = CoroutineScope(Dispatchers.IO).launch {
        userSettingsFirebaseApi.findCurrent()?.let { document ->
            userSettingsRepository.update(
                userSettingsRepository.findCurrent().apply {
                    updateBirthDate(document.birthDate)
                    updateDisplayName(document.displayName)
                    updateWeight(document.height)
                    updateWeight(document.weight)
                    updateMeasureSystem(document.measureSystem)
                    updateSex(document.sex)
                }
            )
        }
    }
}


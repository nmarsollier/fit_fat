package com.nmarsollier.fitfat.userSettings.model

import com.nmarsollier.fitfat.common.firebase.FirebaseConnection
import com.nmarsollier.fitfat.userSettings.model.api.UserSettingsFirebaseApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

internal class DownloadSyncFirebaseService(
    private val userSettingsFirebaseApi: UserSettingsFirebaseApi,
    private val userSettingsRepository: UserSettingsRepository,
    private val firebaseConnection: FirebaseConnection
) {
    init {
        MainScope().launch {
            firebaseConnection.statusFlow.collect {
                if (it) {
                    downloadAndSyncUserSettings()
                }
            }
        }
    }

    private fun downloadAndSyncUserSettings() = MainScope().launch(Dispatchers.IO) {
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


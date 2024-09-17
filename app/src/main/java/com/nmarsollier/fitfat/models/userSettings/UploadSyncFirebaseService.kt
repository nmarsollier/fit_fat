package com.nmarsollier.fitfat.models.userSettings

import com.nmarsollier.fitfat.models.userSettings.api.UserSettingsFirebaseApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal class UploadSyncFirebaseService(
    private val userSettingsFirebaseApi: UserSettingsFirebaseApi,
    private val userSettingsRepository: UserSettingsRepository
) {
    fun uploadUserSettings() = CoroutineScope(Dispatchers.IO).launch {
        userSettingsFirebaseApi.update(userSettingsRepository.findCurrent())
    }
}


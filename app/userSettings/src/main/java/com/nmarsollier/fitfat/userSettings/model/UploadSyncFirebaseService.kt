package com.nmarsollier.fitfat.userSettings.model

import com.nmarsollier.fitfat.userSettings.model.api.UserSettingsFirebaseApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

internal class UploadSyncFirebaseService(
    private val userSettingsFirebaseApi: UserSettingsFirebaseApi,
    private val userSettingsRepository: UserSettingsRepository
) {
    fun uploadUserSettings() = MainScope().launch(Dispatchers.IO) {
        userSettingsFirebaseApi.update(userSettingsRepository.findCurrent())
    }
}


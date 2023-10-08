package com.nmarsollier.fitfat.useCases

import com.nmarsollier.fitfat.model.firebase.UserSettingsDTO
import com.nmarsollier.fitfat.model.firebase.UserSettingsFirebaseRepository
import com.nmarsollier.fitfat.model.userSettings.UserSettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class FirebaseSettingsSyncUseCase(
    private val userSettingsFirebaseRepository: UserSettingsFirebaseRepository,
    private val userSettingsRepository: UserSettingsRepository
) {
    fun downloadAndSyncUserSettings() = MainScope().launch(Dispatchers.IO) {
        userSettingsFirebaseRepository.load().let {
            it?.let {
                updateFromFirebase(it)
            }
        }
    }

    fun uploadUserSettings() = MainScope().launch(Dispatchers.IO) {
        userSettingsRepository.load().let {
            userSettingsFirebaseRepository.save(it)
        }
    }

    private suspend fun updateFromFirebase(document: UserSettingsDTO?) {
        document ?: return
        val userSettings = userSettingsRepository.load()

        document.birthDate?.let {
            userSettings.birthDate = it
        }

        document.displayName?.let {
            userSettings.displayName = it
        }

        document.height?.let {
            userSettings.height = it
        }

        document.weight?.let {
            userSettings.weight = it
        }

        document.measureSystem?.let {
            userSettings.measureSystem = it
        }

        document.sex?.let {
            userSettings.sex = it
        }

        userSettingsRepository.update(userSettings)
    }
}


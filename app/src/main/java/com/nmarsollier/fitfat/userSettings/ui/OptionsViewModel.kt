package com.nmarsollier.fitfat.userSettings.ui

import androidx.activity.ComponentActivity
import androidx.lifecycle.viewModelScope
import com.nmarsollier.fitfat.common.firebase.FirebaseConnection
import com.nmarsollier.fitfat.common.firebase.GoogleAuthResult
import com.nmarsollier.fitfat.common.ui.viewModel.StateViewModel
import com.nmarsollier.fitfat.userSettings.model.UploadSyncFirebaseService
import com.nmarsollier.fitfat.userSettings.model.UserSettings
import com.nmarsollier.fitfat.userSettings.model.UserSettingsRepository
import com.nmarsollier.fitfat.userSettings.model.db.UserSettingsData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date

sealed interface OptionsState {
    data object Loading : OptionsState
    data object GoogleLoginError : OptionsState

    data class Ready(
        val userSettings: UserSettingsData,
        val hasChanged: Boolean
    ) : OptionsState
}

sealed interface OptionsEvent

sealed interface OptionsAction {
    data class LoginWithGoogle(val activity: ComponentActivity) : OptionsAction
    data object DisableFirebase : OptionsAction
    data class UpdateSex(val newSex: UserSettingsData.SexType) : OptionsAction
    data class UpdateMeasureSystem(val system: UserSettingsData.MeasureType) : OptionsAction
    data class UpdateWeight(val newWeight: Double) : OptionsAction
    data class UpdateHeight(val newHeight: Double) : OptionsAction
    data class UpdateDisplayName(val newName: String) : OptionsAction
    data class UpdateBirthDate(val newBirthDate: Date) : OptionsAction
    data object SaveSettings : OptionsAction
    data object Initialize : OptionsAction
}

class OptionsViewModel internal constructor(
    private val userSettingsRepository: UserSettingsRepository,
    private val uploadSyncFirebaseService: UploadSyncFirebaseService,
    private val firebaseConnection: FirebaseConnection
) : StateViewModel<OptionsState, OptionsEvent, OptionsAction>(
    OptionsState.Loading
) {
    private var userSettings: UserSettings? = null

    private val dataChanged: Boolean
        get() = (state.value as? OptionsState.Ready)?.hasChanged == true


    override fun reduce(action: OptionsAction) = when (action) {
        OptionsAction.DisableFirebase -> disableFirebase()
        OptionsAction.Initialize -> load()
        is OptionsAction.LoginWithGoogle -> loginWithGoogle(action)
        OptionsAction.SaveSettings -> saveSettings()
        is OptionsAction.UpdateBirthDate -> updateBirthDate(action)
        is OptionsAction.UpdateDisplayName -> updateDisplayName(action)
        is OptionsAction.UpdateHeight -> updateHeight(action)
        is OptionsAction.UpdateMeasureSystem -> updateMeasureSystem(action)
        is OptionsAction.UpdateSex -> updateSex(action)
        is OptionsAction.UpdateWeight -> updateWeight(action)
    }

    private fun load() {
        OptionsState.Loading.sendToState()
        viewModelScope.launch(Dispatchers.IO) {
            userSettingsRepository.findCurrent().let {
                userSettings = it
                OptionsState.Ready(
                    userSettings = it.value,
                    hasChanged = false
                ).sendToState()
            }
        }
    }

    private fun saveSettings() {
        if (!dataChanged) return
        val userSettings = userSettings ?: return

        viewModelScope.launch(Dispatchers.IO) {
            userSettingsRepository.update(userSettings)
            uploadSyncFirebaseService.uploadUserSettings()
            OptionsState.Ready(
                userSettings = userSettings.value,
                hasChanged = false
            ).sendToState()
        }
    }

    private fun updateBirthDate(event: OptionsAction.UpdateBirthDate) {
        userSettings?.apply {
            updateBirthDate(event.newBirthDate)
            OptionsState.Ready(
                hasChanged = true,
                userSettings = value
            ).sendToState()
        }
    }

    private fun updateDisplayName(event: OptionsAction.UpdateDisplayName) {
        userSettings?.takeIf { it.value.displayName != event.newName }?.apply {
            updateDisplayName(event.newName)
            OptionsState.Ready(
                hasChanged = true,
                userSettings = value
            ).sendToState()
        }
    }

    private fun updateHeight(event: OptionsAction.UpdateHeight) {
        userSettings?.takeIf { it.value.height != event.newHeight }?.apply {
            updateHeight(event.newHeight)
            OptionsState.Ready(
                hasChanged = true,
                userSettings = value
            ).sendToState()
        }
    }

    private fun updateWeight(event: OptionsAction.UpdateWeight) {
        userSettings?.takeIf { it.value.weight != event.newWeight }?.apply {
            updateWeight(event.newWeight)
            OptionsState.Ready(
                hasChanged = true,
                userSettings = value
            ).sendToState()
        }
    }

    private fun updateMeasureSystem(event: OptionsAction.UpdateMeasureSystem) {
        userSettings?.takeIf { it.value.measureSystem != event.system }?.apply {
            updateMeasureSystem(event.system)
            OptionsState.Ready(
                hasChanged = true,
                userSettings = value
            ).sendToState()
        }
    }

    private fun updateSex(event: OptionsAction.UpdateSex) {
        userSettings?.takeIf { it.value.sex != event.newSex }?.apply {
            updateSex(event.newSex)
            OptionsState.Ready(
                hasChanged = true,
                userSettings = value
            ).sendToState()
        }
    }

    private fun disableFirebase() {
        userSettings?.apply {
            updateFirebaseToken(null)
            OptionsState.Ready(
                hasChanged = true,
                userSettings = value
            ).sendToState()
        }
    }

    private fun loginWithGoogle(event: OptionsAction.LoginWithGoogle) {
        viewModelScope.launch(Dispatchers.IO) {
            OptionsState.Loading.sendToState()
            firebaseConnection.signInWithGoogle(event.activity).let {
                when (it) {
                    GoogleAuthResult.Error -> OptionsState.GoogleLoginError.sendToState()
                    is GoogleAuthResult.Success -> {
                        userSettingsRepository.findCurrent().apply {
                            updateFirebaseToken(it.token)
                            userSettingsRepository.update(this)
                        }
                    }
                }
                load()
            }
        }
    }

    companion object
}
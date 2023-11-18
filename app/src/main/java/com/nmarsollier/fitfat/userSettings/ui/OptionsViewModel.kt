package com.nmarsollier.fitfat.userSettings.ui

import androidx.activity.ComponentActivity
import androidx.lifecycle.viewModelScope
import com.nmarsollier.fitfat.common.firebase.FirebaseConnection
import com.nmarsollier.fitfat.common.firebase.GoogleAuthResult
import com.nmarsollier.fitfat.userSettings.model.UploadSyncFirebaseService
import com.nmarsollier.fitfat.userSettings.model.UserSettings
import com.nmarsollier.fitfat.userSettings.model.db.UserSettingsData
import com.nmarsollier.fitfat.userSettings.model.UserSettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date

sealed class OptionsState {
    data object Loading : OptionsState()
    data object GoogleLoginError : OptionsState()

    data class Ready(
        val userSettings: UserSettingsData,
        val hasChanged: Boolean
    ) : OptionsState()
}

sealed class OptionsEvent {
    data class LoginWithGoogle(val activity: ComponentActivity) : OptionsEvent()
    data object DisableFirebase : OptionsEvent()
    data class UpdateSex(val newSex: UserSettingsData.SexType) : OptionsEvent()
    data class UpdateMeasureSystem(val system: UserSettingsData.MeasureType) : OptionsEvent()
    data class UpdateWeight(val newWeight: Double) : OptionsEvent()
    data class UpdateHeight(val newHeight: Double) : OptionsEvent()
    data class UpdateDisplayName(val newName: String) : OptionsEvent()
    data class UpdateBirthDate(val newBirthDate: Date) : OptionsEvent()
    data object SaveSettings : OptionsEvent()
    data object Initialize : OptionsEvent()
}

class OptionsViewModel internal constructor(
    private val userSettingsRepository: UserSettingsRepository,
    private val uploadSyncFirebaseService: UploadSyncFirebaseService,
    private val firebaseConnection: FirebaseConnection
) : com.nmarsollier.fitfat.common.ui.viewModel.BaseViewModel<OptionsState, OptionsEvent>(OptionsState.Loading){
    private var userSettings: UserSettings? = null

    val dataChanged: Boolean
        get() = (state.value as? OptionsState.Ready)?.hasChanged == true


    override fun reduce(event: OptionsEvent) = when (event) {
        OptionsEvent.DisableFirebase ->disableFirebase()
        OptionsEvent.Initialize -> load()
        is OptionsEvent.LoginWithGoogle -> loginWithGoogle(event)
        OptionsEvent.SaveSettings -> saveSettings()
        is OptionsEvent.UpdateBirthDate -> updateBirthDate(event)
        is OptionsEvent.UpdateDisplayName -> updateDisplayName(event)
        is OptionsEvent.UpdateHeight -> updateHeight(event)
        is OptionsEvent.UpdateMeasureSystem -> updateMeasureSystem(event)
        is OptionsEvent.UpdateSex -> updateSex(event)
        is OptionsEvent.UpdateWeight -> updateWeight(event)
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

    private fun updateBirthDate(event: OptionsEvent.UpdateBirthDate) {
        userSettings?.apply {
            updateBirthDate(event.newBirthDate)
            OptionsState.Ready(
                hasChanged = true,
                userSettings = value
            ).sendToState()
        }
    }

    private fun updateDisplayName(event: OptionsEvent.UpdateDisplayName) {
        userSettings?.takeIf { it.value.displayName != event.newName }?.apply {
            updateDisplayName(event.newName)
            OptionsState.Ready(
                hasChanged = true,
                userSettings = value
            ).sendToState()
        }
    }

    private fun updateHeight(event: OptionsEvent.UpdateHeight) {
        userSettings?.takeIf { it.value.height != event.newHeight }?.apply {
            updateHeight(event.newHeight)
            OptionsState.Ready(
                hasChanged = true,
                userSettings = value
            ).sendToState()
        }
    }

    private fun updateWeight(event: OptionsEvent.UpdateWeight) {
        userSettings?.takeIf { it.value.weight != event.newWeight }?.apply {
            updateWeight(event.newWeight)
            OptionsState.Ready(
                hasChanged = true,
                userSettings = value
            ).sendToState()
        }
    }

    private fun updateMeasureSystem(event: OptionsEvent.UpdateMeasureSystem) {
        userSettings?.takeIf { it.value.measureSystem != event.system }?.apply {
            updateMeasureSystem(event.system)
            OptionsState.Ready(
                hasChanged = true,
                userSettings = value
            ).sendToState()
        }
    }

    private fun updateSex(event: OptionsEvent.UpdateSex) {
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

    private fun loginWithGoogle(event: OptionsEvent.LoginWithGoogle) {
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
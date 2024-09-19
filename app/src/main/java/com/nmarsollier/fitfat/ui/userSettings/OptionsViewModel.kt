package com.nmarsollier.fitfat.ui.userSettings

import androidx.activity.*
import androidx.compose.runtime.*
import androidx.lifecycle.*
import com.nmarsollier.fitfat.models.firebase.*
import com.nmarsollier.fitfat.models.userSettings.*
import com.nmarsollier.fitfat.models.userSettings.db.*
import com.nmarsollier.fitfat.ui.common.viewModel.*
import kotlinx.coroutines.*
import java.util.*

sealed interface OptionsState {
    @Stable
    data object Loading : OptionsState

    @Stable
    data class Ready(
        val userSettings: UserSettings, val hasChanged: Boolean
    ) : OptionsState
}

sealed interface OptionsEvent {
    @Stable
    data object ShowGoogleLoginError : OptionsEvent
}

sealed interface OptionsAction {
    @Stable
    data class LoginWithGoogle(val activity: ComponentActivity) : OptionsAction

    @Stable
    data object DisableFirebase : OptionsAction

    @Stable
    data class UpdateSex(val newSex: SexType) : OptionsAction

    @Stable
    data class UpdateMeasureSystem(val system: MeasureType) : OptionsAction

    @Stable
    data class UpdateWeight(val newWeight: Double) : OptionsAction

    @Stable
    data class UpdateHeight(val newHeight: Double) : OptionsAction

    @Stable
    data class UpdateDisplayName(val newName: String) : OptionsAction

    @Stable
    data class UpdateBirthDate(val newBirthDate: Date) : OptionsAction

    @Stable
    data object SaveSettings : OptionsAction
}

class OptionsViewModel internal constructor(
    private val userSettingsRepository: UserSettingsRepository,
    private val uploadSyncFirebaseService: UploadSyncFirebaseService,
    private val firebaseConnection: FirebaseConnection
) : StateViewModel<OptionsState, OptionsEvent, OptionsAction>(
    OptionsState.Loading
) {
    init {
        load()

        viewModelScope.launch(Dispatchers.IO) {
            userSettingsRepository.updateFlow.collect {
                it?.run {
                    load()
                }
            }
        }
    }

    private val dataChanged: Boolean
        get() = (state.value as? OptionsState.Ready)?.hasChanged == true

    override fun reduce(action: OptionsAction) {
        when (action) {
            OptionsAction.DisableFirebase -> disableFirebase()
            is OptionsAction.LoginWithGoogle -> loginWithGoogle(action)
            OptionsAction.SaveSettings -> saveSettings()
            is OptionsAction.UpdateBirthDate -> updateBirthDate(action)
            is OptionsAction.UpdateDisplayName -> updateDisplayName(action)
            is OptionsAction.UpdateHeight -> updateHeight(action)
            is OptionsAction.UpdateMeasureSystem -> updateMeasureSystem(action)
            is OptionsAction.UpdateSex -> updateSex(action)
            is OptionsAction.UpdateWeight -> updateWeight(action)
        }
    }

    private fun load() {
        if (state.value is OptionsState.Ready) return

        OptionsState.Loading.sendToState()

        viewModelScope.launch(Dispatchers.IO) {
            userSettingsRepository.findCurrent().let {
                OptionsState.Ready(
                    userSettings = it, hasChanged = false
                ).sendToState()
            }
        }
    }

    private fun saveSettings() {
        if (!dataChanged) return
        val userSettings = (state.value as? OptionsState.Ready)?.userSettings ?: return

        viewModelScope.launch(Dispatchers.IO) {
            userSettingsRepository.update(userSettings)
            uploadSyncFirebaseService.uploadUserSettings()
            OptionsState.Ready(
                userSettings = userSettings, hasChanged = false
            ).sendToState()
        }
    }

    private fun updateBirthDate(event: OptionsAction.UpdateBirthDate) {
        val userSettings = (state.value as? OptionsState.Ready)?.userSettings ?: return
        OptionsState.Ready(
            hasChanged = true, userSettings = userSettings.updateBirthDate(event.newBirthDate)
        ).sendToState()
    }

    private fun updateDisplayName(event: OptionsAction.UpdateDisplayName) {
        val userSettings =
            (state.value as? OptionsState.Ready)?.userSettings?.takeIf { it.displayName != event.newName }
                ?: return

        OptionsState.Ready(
            hasChanged = true, userSettings = userSettings.updateDisplayName(event.newName)
        ).sendToState()
    }

    private fun updateHeight(event: OptionsAction.UpdateHeight) {
        val userSettings =
            (state.value as? OptionsState.Ready)?.userSettings?.takeIf { it.height != event.newHeight }
                ?: return

        OptionsState.Ready(
            hasChanged = true, userSettings = userSettings.updateHeight(event.newHeight)
        ).sendToState()

    }

    private fun updateWeight(event: OptionsAction.UpdateWeight) {
        val userSettings =
            (state.value as? OptionsState.Ready)?.userSettings?.takeIf { it.weight != event.newWeight }
                ?: return

        OptionsState.Ready(
            hasChanged = true, userSettings = userSettings.updateWeight(event.newWeight)
        ).sendToState()

    }

    private fun updateMeasureSystem(event: OptionsAction.UpdateMeasureSystem) {
        val userSettings =
            (state.value as? OptionsState.Ready)?.userSettings?.takeIf { it.measureSystem != event.system }
                ?: return

        OptionsState.Ready(
            hasChanged = true, userSettings = userSettings.updateMeasureSystem(event.system)
        ).sendToState()
    }

    private fun updateSex(event: OptionsAction.UpdateSex) {
        val userSettings =
            (state.value as? OptionsState.Ready)?.userSettings?.takeIf { it.sex != event.newSex }
                ?: return

        OptionsState.Ready(
            hasChanged = true, userSettings = userSettings.updateSex(event.newSex)
        ).sendToState()
    }

    private fun disableFirebase() {
        val userSettings = (state.value as? OptionsState.Ready)?.userSettings ?: return

        OptionsState.Ready(
            hasChanged = true, userSettings = userSettings.updateFirebaseToken(null)
        ).sendToState()
    }

    private fun loginWithGoogle(event: OptionsAction.LoginWithGoogle) {
        viewModelScope.launch(Dispatchers.IO) {
            OptionsState.Loading.sendToState()
            firebaseConnection.signInWithGoogle(event.activity).let {
                when (it) {
                    GoogleAuthResult.Error -> {
                        OptionsEvent.ShowGoogleLoginError.sendToEvent()
                        load()
                    }

                    is GoogleAuthResult.Success -> {
                        userSettingsRepository.findCurrent().apply {
                            userSettingsRepository.update(updateFirebaseToken(it.token))
                        }
                    }
                }
            }
        }
    }

    companion object
}
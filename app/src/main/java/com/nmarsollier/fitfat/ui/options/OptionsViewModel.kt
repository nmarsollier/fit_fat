package com.nmarsollier.fitfat.ui.options

import androidx.activity.ComponentActivity
import androidx.lifecycle.viewModelScope
import com.nmarsollier.fitfat.common.uiUtils.StateViewModel
import com.nmarsollier.fitfat.models.firebase.FirebaseConnection
import com.nmarsollier.fitfat.models.firebase.GoogleAuthResult
import com.nmarsollier.fitfat.models.userSettings.UploadSyncFirebaseService
import com.nmarsollier.fitfat.models.userSettings.UserSettings
import com.nmarsollier.fitfat.models.userSettings.UserSettingsRepository
import com.nmarsollier.fitfat.models.userSettings.db.MeasureType
import com.nmarsollier.fitfat.models.userSettings.db.SexType
import com.nmarsollier.fitfat.models.userSettings.updateBirthDate
import com.nmarsollier.fitfat.models.userSettings.updateDisplayName
import com.nmarsollier.fitfat.models.userSettings.updateFirebaseToken
import com.nmarsollier.fitfat.models.userSettings.updateHeight
import com.nmarsollier.fitfat.models.userSettings.updateMeasureSystem
import com.nmarsollier.fitfat.models.userSettings.updateSex
import com.nmarsollier.fitfat.models.userSettings.updateWeight
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date

sealed interface OptionsState {
    data object Loading : OptionsState
    data class Ready(
        val userSettings: UserSettings, val hasChanged: Boolean
    ) : OptionsState
}

sealed interface OptionsEvent {
    data object ShowGoogleLoginError : OptionsEvent
}

sealed interface OptionsAction {
    data class LoginWithGoogle(val activity: ComponentActivity) : OptionsAction
    data object DisableFirebase : OptionsAction
    data class UpdateSex(val newSex: SexType) : OptionsAction
    data class UpdateMeasureSystem(val system: MeasureType) : OptionsAction
    data class UpdateWeight(val newWeight: Double) : OptionsAction
    data class UpdateHeight(val newHeight: Double) : OptionsAction
    data class UpdateDisplayName(val newName: String) : OptionsAction
    data class UpdateBirthDate(val newBirthDate: Date) : OptionsAction
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

    override fun reduce(action: OptionsAction) = when (action) {
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

    fun load() {
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
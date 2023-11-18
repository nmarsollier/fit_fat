package com.nmarsollier.fitfat.userSettings.ui.options

import androidx.activity.ComponentActivity
import androidx.lifecycle.viewModelScope
import com.nmarsollier.fitfat.firebase.FirebaseConnection
import com.nmarsollier.fitfat.firebase.GoogleAuthResult
import com.nmarsollier.fitfat.userSettings.model.UploadSyncFirebaseService
import com.nmarsollier.fitfat.userSettings.model.UserSettings
import com.nmarsollier.fitfat.userSettings.model.db.UserSettingsData
import com.nmarsollier.fitfat.userSettings.model.UserSettingsRepository
import com.nmarsollier.fitfat.utils.ui.viewModel.BaseViewModel
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

interface OptionsReducer {
    fun loginWithGoogle(activity: ComponentActivity)
    fun disableFirebase()
    fun updateSex(newSex: UserSettingsData.SexType)
    fun updateMeasureSystem(system: UserSettingsData.MeasureType)
    fun updateWeight(newWeight: Double)
    fun updateHeight(newHeight: Double)
    fun updateDisplayName(newName: String)
    fun updateBirthDate(newBirthDate: Date)
    fun saveSettings()
    fun load()
}

class OptionsViewModel internal constructor(
    private val userSettingsRepository: UserSettingsRepository,
    private val uploadSyncFirebaseService: UploadSyncFirebaseService,
    private val firebaseConnection: FirebaseConnection
) : com.nmarsollier.fitfat.utils.ui.viewModel.BaseViewModel<OptionsState>(OptionsState.Loading),
    OptionsReducer {
    private var userSettings: UserSettings? = null

    val dataChanged: Boolean
        get() = (state.value as? OptionsState.Ready)?.hasChanged == true

    override fun load() {
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

    override fun saveSettings() {
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

    override fun updateBirthDate(newBirthDate: Date) {
        userSettings?.apply {
            updateBirthDate(newBirthDate)
            OptionsState.Ready(
                hasChanged = true,
                userSettings = value
            ).sendToState()
        }
    }

    override fun updateDisplayName(newName: String) {
        userSettings?.takeIf { it.value.displayName != newName }?.apply {
            updateDisplayName(newName)
            OptionsState.Ready(
                hasChanged = true,
                userSettings = value
            ).sendToState()
        }
    }

    override fun updateHeight(newHeight: Double) {
        userSettings?.takeIf { it.value.height != newHeight }?.apply {
            updateHeight(newHeight)
            OptionsState.Ready(
                hasChanged = true,
                userSettings = value
            ).sendToState()
        }
    }

    override fun updateWeight(newWeight: Double) {
        userSettings?.takeIf { it.value.weight != newWeight }?.apply {
            updateWeight(newWeight)
            OptionsState.Ready(
                hasChanged = true,
                userSettings = value
            ).sendToState()
        }
    }

    override fun updateMeasureSystem(system: UserSettingsData.MeasureType) {
        userSettings?.takeIf { it.value.measureSystem != system }?.apply {
            updateMeasureSystem(system)
            OptionsState.Ready(
                hasChanged = true,
                userSettings = value
            ).sendToState()
        }
    }

    override fun updateSex(newSex: UserSettingsData.SexType) {
        userSettings?.takeIf { it.value.sex != newSex }?.apply {
            updateSex(newSex)
            OptionsState.Ready(
                hasChanged = true,
                userSettings = value
            ).sendToState()
        }
    }

    override fun disableFirebase() {
        userSettings?.apply {
            updateFirebaseToken(null)
            OptionsState.Ready(
                hasChanged = true,
                userSettings = value
            ).sendToState()
        }
    }

    override fun loginWithGoogle(activity: ComponentActivity) {
        viewModelScope.launch(Dispatchers.IO) {
            OptionsState.Loading.sendToState()
            firebaseConnection.signInWithGoogle(activity).let {
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
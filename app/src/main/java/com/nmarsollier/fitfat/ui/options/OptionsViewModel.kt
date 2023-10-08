package com.nmarsollier.fitfat.ui.options

import androidx.activity.ComponentActivity
import androidx.lifecycle.viewModelScope
import com.nmarsollier.fitfat.model.firebase.GoogleAuthResult
import com.nmarsollier.fitfat.model.userSettings.UserSettingsEntity
import com.nmarsollier.fitfat.model.userSettings.UserSettingsEntity.MeasureType
import com.nmarsollier.fitfat.model.userSettings.UserSettingsEntity.SexType
import com.nmarsollier.fitfat.model.userSettings.UserSettingsRepository
import com.nmarsollier.fitfat.useCases.FirebaseLoginUseCase
import com.nmarsollier.fitfat.useCases.FirebaseSettingsSyncUseCase
import com.nmarsollier.fitfat.utils.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date

sealed class OptionsState {
    data object Loading : OptionsState()
    data object GoogleLoginError : OptionsState()

    data class Ready(
        val userSettingsEntity: UserSettingsEntity,
        val hasChanged: Boolean
    ) : OptionsState()
}

interface OptionsReducer {
    fun loginWithGoogle(activity: ComponentActivity)
    fun disableFirebase()
    fun updateSex(newSex: SexType)
    fun updateMeasureSystem(system: MeasureType)
    fun updateWeight(newWeight: Double)
    fun updateHeight(newHeight: Double)
    fun updateDisplayName(newName: String)
    fun updateBirthDate(newBirthDate: Date)
    fun saveSettings()
    fun load()
}

class OptionsViewModel(
    private val userSettingsRepository: UserSettingsRepository,
    private val firebaseSettingsSyncUseCase: FirebaseSettingsSyncUseCase,
    private val firebaseLoginUseCase: FirebaseLoginUseCase
) : BaseViewModel<OptionsState>(OptionsState.Loading), OptionsReducer {
    private val currentUserSettingsEntity: UserSettingsEntity?
        get() = (state.value as? OptionsState.Ready)?.userSettingsEntity

    private val dataChanged: Boolean
        get() = (state.value as? OptionsState.Ready)?.hasChanged == true

    override fun load() {
        mutableState.update { OptionsState.Loading }
        viewModelScope.launch(Dispatchers.IO) {
            mutableState.update {
                val us = userSettingsRepository.load()
                OptionsState.Ready(
                    userSettingsEntity = us, hasChanged = false
                )
            }
        }
    }

    override fun saveSettings() {
        if (!dataChanged) return
        val userSettings = currentUserSettingsEntity ?: return

        viewModelScope.launch {
            userSettingsRepository.update(userSettings)
            firebaseSettingsSyncUseCase.uploadUserSettings()
            mutableState.update {
                OptionsState.Ready(
                    userSettingsEntity = userSettings, hasChanged = false
                )
            }
        }
    }

    override fun updateBirthDate(newBirthDate: Date) {
        currentUserSettingsEntity?.let { us ->
            mutableState.update {
                OptionsState.Ready(
                    hasChanged = true, userSettingsEntity = us.copy(
                        birthDate = newBirthDate
                    )
                )
            }
        }
    }

    override fun updateDisplayName(newName: String) {
        currentUserSettingsEntity?.takeIf { it.displayName != newName }?.let { us ->
            mutableState.update {
                OptionsState.Ready(
                    hasChanged = true, userSettingsEntity = us.copy(
                        displayName = newName
                    )
                )
            }
        }
    }

    override fun updateHeight(newHeight: Double) {
        currentUserSettingsEntity?.takeIf { it.height != newHeight }?.let { us ->
            mutableState.update {
                OptionsState.Ready(
                    hasChanged = true, userSettingsEntity = us.copy(
                        height = newHeight
                    )
                )
            }
        }
    }

    override fun updateWeight(newWeight: Double) {
        currentUserSettingsEntity?.takeIf { it.weight != newWeight }?.let { us ->
            mutableState.update {
                OptionsState.Ready(
                    hasChanged = true, userSettingsEntity = us.copy(
                        weight = newWeight
                    )
                )
            }
        }
    }

    override fun updateMeasureSystem(system: MeasureType) {
        currentUserSettingsEntity?.takeIf { it.measureSystem != system }?.let { us ->
            mutableState.update {
                OptionsState.Ready(
                    hasChanged = true, userSettingsEntity = us.copy(
                        measureSystem = system
                    )
                )
            }
        }
    }

    override fun updateSex(newSex: SexType) {
        currentUserSettingsEntity?.takeIf { it.sex != newSex }?.let { us ->
            mutableState.update {
                OptionsState.Ready(
                    hasChanged = true, userSettingsEntity = us.copy(
                        sex = newSex
                    )
                )
            }
        }
    }

    override fun disableFirebase() {
        viewModelScope.launch(Dispatchers.IO) {
            userSettingsRepository.updateFirebaseToken(null)
            load()
        }
    }

    override fun loginWithGoogle(activity: ComponentActivity) {
        viewModelScope.launch(Dispatchers.IO) {
            mutableState.update { OptionsState.Loading }
            firebaseLoginUseCase.googleLoginAndSync(activity).let {
                if (it is GoogleAuthResult.Error) {
                    mutableState.update { OptionsState.GoogleLoginError }
                }
                load()
            }
        }
    }

    companion object
}
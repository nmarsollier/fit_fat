package com.nmarsollier.fitfat.ui.options

import androidx.lifecycle.viewModelScope
import com.nmarsollier.fitfat.model.firebase.FirebaseRepository
import com.nmarsollier.fitfat.model.userSettings.MeasureType
import com.nmarsollier.fitfat.model.userSettings.SexType
import com.nmarsollier.fitfat.model.userSettings.UserSettings
import com.nmarsollier.fitfat.model.userSettings.UserSettingsRepository
import com.nmarsollier.fitfat.useCases.FirebaseUseCase
import com.nmarsollier.fitfat.useCases.GoogleLoginResult
import com.nmarsollier.fitfat.utils.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

sealed class OptionsState {
    object Loading : OptionsState()
    object GoogleLoginError : OptionsState()

    data class Ready(
        val userSettings: UserSettings,
        val hasChanged: Boolean,
    ) : OptionsState()
}

@HiltViewModel
class OptionsViewModel @Inject constructor(
    private val userSettingsRepository: UserSettingsRepository,
    private val firebaseRepository: FirebaseRepository,
    private val firebaseUseCase: FirebaseUseCase
) : BaseViewModel<OptionsState>(OptionsState.Loading) {
    fun load() {
        viewModelScope.launch(Dispatchers.IO) {
            userSettingsRepository.load().let { state ->
                mutableState.update {
                    OptionsState.Ready(
                        hasChanged = false, userSettings = state
                    )
                }
            }
        }
    }

    private val currentUserSettings: UserSettings?
        get() = (state.value as? OptionsState.Ready)?.userSettings

    val dataChanged: Boolean
        get() = (state.value as? OptionsState.Ready)?.hasChanged == true

    fun saveSettings() {
        if (!dataChanged) return
        val userSettings = currentUserSettings ?: return

        viewModelScope.launch {
            userSettingsRepository.update(userSettings)
            firebaseRepository.save(userSettings).let { state ->
                mutableState.update {
                    OptionsState.Ready(
                        userSettings = state, hasChanged = false
                    )
                }
            }
        }
    }

    fun updateBirthDate(newBirthDate: Date) {
        currentUserSettings?.let { us ->
            mutableState.update {
                OptionsState.Ready(
                    hasChanged = true, userSettings = us.copy(
                        birthDate = newBirthDate
                    )
                )
            }
        }
    }

    fun updateDisplayName(newName: String) {
        currentUserSettings?.takeIf { it.displayName != newName }?.let { us ->
            mutableState.update {
                OptionsState.Ready(
                    hasChanged = true, userSettings = us.copy(
                        displayName = newName
                    )
                )
            }
        }
    }

    fun updateHeight(newHeight: Double) {
        currentUserSettings?.takeIf { it.height != newHeight }?.let { us ->
            mutableState.update {
                OptionsState.Ready(
                    hasChanged = true, userSettings = us.copy(
                        height = newHeight
                    )
                )
            }
        }
    }

    fun updateWeight(newWeight: Double) {
        currentUserSettings?.takeIf { it.weight != newWeight }?.let { us ->
            mutableState.update {
                OptionsState.Ready(
                    hasChanged = true, userSettings = us.copy(
                        weight = newWeight
                    )
                )
            }
        }
    }

    fun updateMeasureSystem(system: MeasureType) {
        currentUserSettings?.takeIf { it.measureSystem != system }?.let { us ->
            mutableState.update {
                OptionsState.Ready(
                    hasChanged = true, userSettings = us.copy(
                        measureSystem = system
                    )
                )
            }
        }
    }

    fun updateSex(newSex: SexType) {
        currentUserSettings?.takeIf { it.sex != newSex }?.let { us ->
            mutableState.update {
                OptionsState.Ready(
                    hasChanged = true, userSettings = us.copy(
                        sex = newSex
                    )
                )
            }
        }
    }

    fun disableFirebase() {
        currentUserSettings?.takeIf { it.firebaseToken != null }?.let { us ->
            mutableState.update {
                OptionsState.Ready(
                    hasChanged = true, userSettings = us.copy(
                        firebaseToken = null
                    )
                )
            }
            viewModelScope.launch {
                userSettingsRepository.updateFirebaseToken(null)
            }
        }
    }

    fun loginWithGoogle(fragment: OptionsFragment) = viewModelScope.launch {
        val currentState = state.value

        mutableState.update { OptionsState.Loading }
        firebaseUseCase.googleLoginAndSync(fragment).let {
            when (it) {
                is GoogleLoginResult.Error -> {
                    mutableState.update { OptionsState.GoogleLoginError }
                    mutableState.update { currentState }
                }

                else -> {
                    mutableState.update { currentState }
                }
            }
        }
    }
}
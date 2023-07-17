package com.nmarsollier.fitfat.ui.options

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.nmarsollier.fitfat.model.MeasureType
import com.nmarsollier.fitfat.model.SexType
import com.nmarsollier.fitfat.model.UserSettings
import com.nmarsollier.fitfat.model.UserSettingsRepository
import com.nmarsollier.fitfat.ui.utils.BaseViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*

sealed class OptionsState {
    object Initial : OptionsState()
    object Loading : OptionsState()

    data class Ready(
        val userSettings: UserSettings,
        val hasChanged: Boolean,
    ) : OptionsState()
}

class OptionsViewModel : BaseViewModel<OptionsState>(OptionsState.Initial) {
    fun load(
        context: Context
    ) = viewModelScope.launch {
        mutableState.update {
            OptionsState.Loading
        }

        UserSettingsRepository.load(context).firstOrNull { state ->
            mutableState.update {
                OptionsState.Ready(
                    hasChanged = false,
                    userSettings = state
                )
            }
            true
        }
    }

    val currentUserSettings: UserSettings?
        get() = (state.value as? OptionsState.Ready)?.userSettings

    val dataChanged: Boolean
        get() = (state.value as? OptionsState.Ready)?.hasChanged == true

    fun saveSettings(context: Context) {
        if (!dataChanged) return
        val userSettings = currentUserSettings ?: return

        viewModelScope.launch {
            UserSettingsRepository.save(context, userSettings).firstOrNull { state ->
                mutableState.update {
                    OptionsState.Ready(
                        userSettings = state,
                        hasChanged = false
                    )
                }
                true
            }
        }
    }

    fun updateBirthDate(newBirthDate: Date) {
        currentUserSettings?.let { us ->
            mutableState.update {
                OptionsState.Ready(
                    hasChanged = true,
                    userSettings = us.copy(
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
                    hasChanged = true,
                    userSettings = us.copy(
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
                    hasChanged = true,
                    userSettings = us.copy(
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
                    hasChanged = true,
                    userSettings = us.copy(
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
                    hasChanged = true,
                    userSettings = us.copy(
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
                    hasChanged = true,
                    userSettings = us.copy(
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
                    hasChanged = true,
                    userSettings = us.copy(
                        firebaseToken = null
                    )
                )
            }
        }
    }
}
package com.nmarsollier.fitfat.ui.options

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.nmarsollier.fitfat.model.MeasureType
import com.nmarsollier.fitfat.model.SexType
import com.nmarsollier.fitfat.model.UserSettings
import com.nmarsollier.fitfat.model.UserSettingsRepository
import com.nmarsollier.fitfat.ui.utils.BaseViewModel
import kotlinx.coroutines.flow.firstOrNull
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
        state.emit(OptionsState.Loading)

        UserSettingsRepository.load(context).firstOrNull {
            state.emit(
                OptionsState.Ready(
                    hasChanged = false,
                    userSettings = it
                )
            )
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
            UserSettingsRepository.save(context, userSettings).firstOrNull {
                state.emit(
                    OptionsState.Ready(
                        userSettings = it,
                        hasChanged = false
                    )
                )
                true
            }
        }
    }

    fun updateBirthDate(newBirthDate: Date) {
        currentUserSettings?.let {
            state.emit(
                OptionsState.Ready(
                    hasChanged = true,
                    userSettings = it.copy(
                        birthDate = newBirthDate
                    )
                )
            )
        }
    }

    fun updateDisplayName(newName: String) {
        currentUserSettings?.takeIf { it.displayName != newName }?.let {
            state.emit(
                OptionsState.Ready(
                    hasChanged = true,
                    userSettings = it.copy(
                        displayName = newName
                    )
                )
            )
        }
    }

    fun updateHeight(newHeight: Double) {
        currentUserSettings?.takeIf { it.height != newHeight }?.let {
            state.emit(
                OptionsState.Ready(
                    hasChanged = true,
                    userSettings = it.copy(
                        height = newHeight
                    )
                )
            )
        }
    }

    fun updateWeight(newWeight: Double) {
        currentUserSettings?.takeIf { it.weight != newWeight }?.let {
            state.emit(
                OptionsState.Ready(
                    hasChanged = true,
                    userSettings = it.copy(
                        weight = newWeight
                    )
                )
            )
        }
    }

    fun updateMeasureSystem(system: MeasureType) {
        currentUserSettings?.takeIf { it.measureSystem != system }?.let {
            state.emit(
                OptionsState.Ready(
                    hasChanged = true,
                    userSettings = it.copy(
                        measureSystem = system
                    )
                )
            )
        }
    }

    fun updateSex(newSex: SexType) {
        currentUserSettings?.takeIf { it.sex != newSex }?.let {
            state.emit(
                OptionsState.Ready(
                    hasChanged = true,
                    userSettings = it.copy(
                        sex = newSex
                    )
                )
            )
        }
    }

    fun disableFirebase() {
        currentUserSettings?.takeIf { it.firebaseToken != null }?.let {
            state.emit(
                OptionsState.Ready(
                    hasChanged = true,
                    userSettings = it.copy(
                        firebaseToken = null
                    )
                )
            )
        }
    }
}
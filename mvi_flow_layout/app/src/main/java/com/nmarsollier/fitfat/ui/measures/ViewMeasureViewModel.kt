package com.nmarsollier.fitfat.ui.measures

import androidx.lifecycle.viewModelScope
import com.nmarsollier.fitfat.model.measures.Measure
import com.nmarsollier.fitfat.model.userSettings.UserSettings
import com.nmarsollier.fitfat.model.userSettings.UserSettingsRepository
import com.nmarsollier.fitfat.utils.BaseViewModel
import com.nmarsollier.fitfat.utils.collectOnce
import com.nmarsollier.fitfat.utils.ifNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class ViewMeasureState {
    abstract val measure: Measure?

    data class Loading(
        override val measure: Measure? = null
    ) : ViewMeasureState()

    data class Ready(
        override val measure: Measure,
        val userSettings: UserSettings
    ) : ViewMeasureState()
}

class ViewMeasureViewModel : BaseViewModel<ViewMeasureState>(ViewMeasureState.Loading()) {
    private var userSettings: UserSettings? = null
    private var measure: Measure? = null

    fun initialize(newMeasure: Measure) = viewModelScope.launch {
        UserSettingsRepository.load().collectOnce {
            userSettings = it
            updateState()
        }

        measure = newMeasure
        updateState()
    }

    fun updateState() {
        ifNotNull(measure, userSettings) { measure, userSettings ->
            mutableState.update {
                ViewMeasureState.Ready(measure, userSettings)
            }
        }
    }
}
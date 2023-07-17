package com.nmarsollier.fitfat.ui.measures

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.nmarsollier.fitfat.model.Measure
import com.nmarsollier.fitfat.model.UserSettings
import com.nmarsollier.fitfat.model.UserSettingsRepository
import com.nmarsollier.fitfat.ui.utils.BaseViewModel
import com.nmarsollier.fitfat.utils.ifNotNull
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

sealed class ViewMeasureState {
    abstract val measure: Measure?

    data class Initial(
        override val measure: Measure? = null
    ) : ViewMeasureState()

    data class Ready(
        override val measure: Measure,
        val userSettings: UserSettings
    ) : ViewMeasureState()
}

class ViewMeasureViewModel : BaseViewModel<ViewMeasureState>(ViewMeasureState.Initial()) {
    private var userSettings: UserSettings? = null
    private var measure: Measure? = null

    fun initialize(context: Context, newMeasre: Measure) = viewModelScope.launch {
        UserSettingsRepository.load(context).firstOrNull {
            userSettings = it
            updateState()
            true
        }

        measure = newMeasre
        updateState()
    }

    fun updateState() {
        ifNotNull(measure, userSettings) { measure, userSettings ->
            state.emit(ViewMeasureState.Ready(measure, userSettings))
        }
    }
}
package com.nmarsollier.fitfat.measures.ui.edit

import androidx.lifecycle.viewModelScope
import com.nmarsollier.fitfat.common.ui.viewModel.StateViewModel
import com.nmarsollier.fitfat.measures.model.Measure
import com.nmarsollier.fitfat.measures.model.MeasuresRepository
import com.nmarsollier.fitfat.measures.model.SaveMeasureAndUserSettingsService
import com.nmarsollier.fitfat.measures.model.db.MeasureData
import com.nmarsollier.fitfat.measures.model.db.MeasureMethod
import com.nmarsollier.fitfat.measures.model.db.MeasureValue
import com.nmarsollier.fitfat.userSettings.model.UserSettings
import com.nmarsollier.fitfat.userSettings.model.UserSettingsRepository
import com.nmarsollier.fitfat.userSettings.model.db.UserSettingsData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date

sealed interface EditMeasureState {
    data class Loading(
        val measure: MeasureData?, val readOnly: Boolean
    ) : EditMeasureState

    data object Invalid : EditMeasureState

    data class Ready(
        val userSettings: UserSettingsData,
        val measure: MeasureData,
        val showHelp: Int?,
        val showMeasureMethod: Boolean,
        val readOnly: Boolean
    ) : EditMeasureState

    val currentReadOnly: Boolean
        get() = when (this) {
            is Loading -> readOnly
            is Ready -> readOnly
            else -> true
        }
}

sealed interface EditMeasureEvent {
    data object Close : EditMeasureEvent
}

sealed interface EditMeasureAction {
    data object SaveMeasure : EditMeasureAction
    data class UpdateDate(val time: Date) : EditMeasureAction
    data class UpdateMeasureMethod(val measureMethod: MeasureMethod) : EditMeasureAction

    data class UpdateMeasureValue(
        val measureValue: MeasureValue,
        val value: Number
    ) : EditMeasureAction

    data object Close : EditMeasureAction
    data class ToggleHelp(val res: Int?) : EditMeasureAction
    data object ToggleMeasureMethod : EditMeasureAction
    data class Initialize(val initialMeasure: MeasureData?) : EditMeasureAction
}

class EditMeasureViewModel(
    private val measuresRepository: MeasuresRepository,
    private val userSettingsRepository: UserSettingsRepository,
    private val saveMeasureAndUserSettingsService: SaveMeasureAndUserSettingsService
) : StateViewModel<EditMeasureState, EditMeasureEvent, EditMeasureAction>(
    EditMeasureState.Loading(
        null,
        false
    )
) {
    private var userSettings: UserSettings? = null
    private var measure: Measure? = null

    override fun reduce(action: EditMeasureAction) = when (action) {
        EditMeasureAction.Close -> close()
        is EditMeasureAction.Initialize -> init(action)
        EditMeasureAction.SaveMeasure -> saveMeasure()
        is EditMeasureAction.ToggleHelp -> toggleHelp(action)
        EditMeasureAction.ToggleMeasureMethod -> toggleMeasureMethod()
        is EditMeasureAction.UpdateDate -> updateDate(action)
        is EditMeasureAction.UpdateMeasureMethod -> updateMeasureMethod(action)
        is EditMeasureAction.UpdateMeasureValue -> updateMeasureValue(action)
    }

    private fun saveMeasure() {
        viewModelScope.launch {
            val currentState = state.value
            val measure = measure ?: return@launch
            val readOnly = state.value.currentReadOnly
            val userSettings = userSettings ?: return@launch

            EditMeasureState.Loading(null, readOnly).sendToState()

            if (saveMeasureAndUserSettingsService.saveMeasure(measure, userSettings)) {
                close()
            } else {
                EditMeasureState.Invalid.sendToState()
                currentState.sendToState()
            }
        }
    }

    private fun updateDate(event: EditMeasureAction.UpdateDate) {
        if (state.value.currentReadOnly) return
        val measure = measure ?: return

        (state.value as? EditMeasureState.Ready)?.apply {
            measure.updateDate(event.time)
            copy(
                measure = measure.value
            ).sendToState()
        }
    }

    private fun updateMeasureMethod(event: EditMeasureAction.UpdateMeasureMethod) {
        if (state.value.currentReadOnly) return
        val measure = measure ?: return

        (state.value as? EditMeasureState.Ready)?.apply {
            measure.updateMeasureMethod(event.measureMethod)
            copy(
                measure = measure.value,
                showMeasureMethod = false
            ).sendToState()
        }
    }

    private fun updateMeasureValue(event: EditMeasureAction.UpdateMeasureValue) {
        if (state.value.currentReadOnly) return
        val measure = measure ?: return

        (state.value as? EditMeasureState.Ready)?.apply {
            measure.updateMethodValue(event.measureValue, event.value)
            copy(
                measure = measure.value,
                showMeasureMethod = false
            ).sendToState()
        }
    }

    private fun close() {
        EditMeasureEvent.Close.sendToEvent()
    }

    private fun toggleHelp(event: EditMeasureAction.ToggleHelp) {
        val newState = (state.value as? EditMeasureState.Ready) ?: return
        newState.copy(showHelp = event.res).sendToState()
    }

    private fun toggleMeasureMethod() {
        if (state.value.currentReadOnly) return
        val newState = (state.value as? EditMeasureState.Ready) ?: return

        newState.copy(showMeasureMethod = !newState.showMeasureMethod).sendToState()
    }

    private fun init(event: EditMeasureAction.Initialize) {
        EditMeasureState.Loading(
            event.initialMeasure, event.initialMeasure != null
        ).sendToState()

        viewModelScope.launch(Dispatchers.IO) {
            val userSettingsLoaded = userSettingsRepository.findCurrent()
            userSettings = userSettingsLoaded
            val lastMeasure = measuresRepository.findLast()

            val measureLoaded = event.initialMeasure?.let { measuresRepository.findById(it.uid) }
                ?: lastMeasure?.let { Measure.newMeasure(it) }
                ?: Measure.newMeasure(userSettingsLoaded)
            measure = measureLoaded

            EditMeasureState.Ready(
                userSettings = userSettingsLoaded.value,
                measure = measureLoaded.value,
                showHelp = null,
                showMeasureMethod = false,
                readOnly = event.initialMeasure != null
            ).sendToState()
        }
    }

    companion object

}
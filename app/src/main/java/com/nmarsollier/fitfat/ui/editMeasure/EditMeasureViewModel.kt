package com.nmarsollier.fitfat.ui.editMeasure

import androidx.lifecycle.viewModelScope
import com.nmarsollier.fitfat.common.uiUtils.StateViewModel
import com.nmarsollier.fitfat.models.measures.Measure
import com.nmarsollier.fitfat.models.measures.MeasuresRepository
import com.nmarsollier.fitfat.models.measures.SaveMeasureAndUserSettingsService
import com.nmarsollier.fitfat.models.measures.db.MeasureMethod
import com.nmarsollier.fitfat.models.measures.db.MeasureValue
import com.nmarsollier.fitfat.models.measures.isValid
import com.nmarsollier.fitfat.models.measures.updateDate
import com.nmarsollier.fitfat.models.measures.updateMeasureMethod
import com.nmarsollier.fitfat.models.measures.updateMethodValue
import com.nmarsollier.fitfat.models.userSettings.UserSettings
import com.nmarsollier.fitfat.models.userSettings.UserSettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date

sealed interface EditMeasureState {
    data class Loading(
        val measure: Measure?, val readOnly: Boolean
    ) : EditMeasureState


    data class Ready(
        val isSaveEnabled: Boolean,
        val userSettings: UserSettings,
        val measure: Measure,
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
    data object Invalid : EditMeasureEvent
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
    data class Initialize(val initialMeasure: Measure?) : EditMeasureAction
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
    init {
        println("EditMeasureViewModel created")
    }

    private var userSettings: UserSettings? = null

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
        val measure = (state.value as? EditMeasureState.Ready)?.measure ?: return
        viewModelScope.launch {
            val currentState = state.value
            val readOnly = state.value.currentReadOnly
            val userSettings = userSettings ?: return@launch

            EditMeasureState.Loading(null, readOnly).sendToState()

            if (saveMeasureAndUserSettingsService.saveMeasure(measure, userSettings)) {
                close()
            } else {
                EditMeasureEvent.Invalid.sendToEvent()
                currentState.sendToState()
            }
        }
    }

    private fun updateDate(event: EditMeasureAction.UpdateDate) {
        if (state.value.currentReadOnly) return
        val measure = (state.value as? EditMeasureState.Ready)?.measure ?: return
        val readyState = (state.value as? EditMeasureState.Ready) ?: return

        measure.updateDate(event.time).let {
            readyState.copy(
                measure = it,
                isSaveEnabled = it.isValid
            ).sendToState()
        }
    }

    private fun updateMeasureMethod(event: EditMeasureAction.UpdateMeasureMethod) {
        if (state.value.currentReadOnly) return
        val measure = (state.value as? EditMeasureState.Ready)?.measure ?: return
        val readyState = (state.value as? EditMeasureState.Ready) ?: return

        measure.updateMeasureMethod(event.measureMethod).let {
            readyState.copy(
                measure = it,
                showMeasureMethod = false,
                isSaveEnabled = it.isValid
            ).sendToState()
        }
    }

    private fun updateMeasureValue(event: EditMeasureAction.UpdateMeasureValue) {
        if (state.value.currentReadOnly) return
        val measure = (state.value as? EditMeasureState.Ready)?.measure ?: return
        val readyState = (state.value as? EditMeasureState.Ready) ?: return

        measure.updateMethodValue(event.measureValue, event.value).let {
            readyState.copy(
                measure = it,
                showMeasureMethod = false,
                isSaveEnabled = it.isValid
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

            EditMeasureState.Ready(
                isSaveEnabled = measureLoaded.isValid,
                userSettings = userSettingsLoaded,
                measure = measureLoaded,
                showHelp = null,
                showMeasureMethod = false,
                readOnly = event.initialMeasure != null
            ).sendToState()
        }
    }

    companion object

}
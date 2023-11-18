package com.nmarsollier.fitfat.measures.ui.edit

import androidx.lifecycle.viewModelScope
import com.nmarsollier.fitfat.measures.model.Measure
import com.nmarsollier.fitfat.measures.model.MeasuresRepository
import com.nmarsollier.fitfat.measures.model.SaveMeasureAndUserSettingsService
import com.nmarsollier.fitfat.measures.model.db.MeasureData
import com.nmarsollier.fitfat.measures.model.db.MeasureMethod
import com.nmarsollier.fitfat.measures.model.db.MeasureValue
import com.nmarsollier.fitfat.userSettings.model.UserSettings
import com.nmarsollier.fitfat.userSettings.model.UserSettingsRepository
import com.nmarsollier.fitfat.userSettings.model.db.UserSettingsData
import com.nmarsollier.fitfat.common.ui.viewModel.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date

sealed class EditMeasureState {
    data class Loading(
        val measure: MeasureData?, val readOnly: Boolean
    ) : EditMeasureState()

    data object Invalid : EditMeasureState()
    data object Close : EditMeasureState()

    data class Ready(
        val userSettings: UserSettingsData,
        val measure: MeasureData,
        val showHelp: Int?,
        val showMethod: Boolean,
        val readOnly: Boolean
    ) : EditMeasureState()

    val currentReadOnly: Boolean
        get() = when (this) {
            is Loading -> readOnly
            is Ready -> readOnly
            else -> true
        }
}

sealed class EditMeasureEvent {
    data object SaveMeasure : EditMeasureEvent()
    data class UpdateDate(val time: Date) : EditMeasureEvent()
    data class UpdateMeasureMethod(val measureMethod: MeasureMethod) : EditMeasureEvent()

    data class UpdateMeasureValue(
        val measureValue: MeasureValue,
        val value: Number
    ) : EditMeasureEvent()

    data object Close : EditMeasureEvent()
    data class ToggleHelp(val res: Int?) : EditMeasureEvent()
    data object ToggleShowMethod : EditMeasureEvent()
    data class Initialize(val initialMeasure: MeasureData?) : EditMeasureEvent()
}

class EditMeasureViewModel(
    private val measuresRepository: MeasuresRepository,
    private val userSettingsRepository: UserSettingsRepository,
    private val saveMeasureAndUserSettingsService: SaveMeasureAndUserSettingsService
) : BaseViewModel<EditMeasureState, EditMeasureEvent>(EditMeasureState.Loading(null, false)) {
    private var userSettings: UserSettings? = null
    private var measure: Measure? = null


    override fun reduce(event: EditMeasureEvent) = when (event) {
        EditMeasureEvent.Close -> close()
        is EditMeasureEvent.Initialize -> init(event)
        EditMeasureEvent.SaveMeasure -> saveMeasure()
        is EditMeasureEvent.ToggleHelp -> toggleHelp(event)
        EditMeasureEvent.ToggleShowMethod -> toggleShowMethod()
        is EditMeasureEvent.UpdateDate -> updateDate(event)
        is EditMeasureEvent.UpdateMeasureMethod -> updateMeasureMethod(event)
        is EditMeasureEvent.UpdateMeasureValue -> updateMeasureValue(event)
    }

    private fun saveMeasure() {
        viewModelScope.launch {
            val currentState = state.value
            val measure = measure ?: return@launch
            val readOnly = state.value.currentReadOnly
            val userSettings = userSettings ?: return@launch

            EditMeasureState.Loading(null, readOnly).sendToState()

            if (saveMeasureAndUserSettingsService.saveMeasure(measure, userSettings)) {
                EditMeasureState.Close.sendToState()
            } else {
                EditMeasureState.Invalid.sendToState()
                currentState.sendToState()
            }
        }
    }

    private fun updateDate(event: EditMeasureEvent.UpdateDate) {
        if (state.value.currentReadOnly) return
        val measure = measure ?: return

        (state.value as? EditMeasureState.Ready)?.apply {
            measure.updateDate(event.time)
            copy(
                measure = measure.value
            ).sendToState()
        }
    }

    private fun updateMeasureMethod(event: EditMeasureEvent.UpdateMeasureMethod) {
        if (state.value.currentReadOnly) return
        val measure = measure ?: return

        (state.value as? EditMeasureState.Ready)?.apply {
            measure.updateMeasureMethod(event.measureMethod)
            copy(
                measure = measure.value,
                showMethod = false
            ).sendToState()
        }
    }

    private fun updateMeasureValue(event: EditMeasureEvent.UpdateMeasureValue) {
        if (state.value.currentReadOnly) return
        val measure = measure ?: return

        (state.value as? EditMeasureState.Ready)?.apply {
            measure.updateMethodValue(event.measureValue, event.value)
            copy(
                measure = measure.value,
                showMethod = false
            ).sendToState()
        }
    }

    private fun close() {
        EditMeasureState.Close.sendToState()
    }

    private fun toggleHelp(event: EditMeasureEvent.ToggleHelp) {
        val newState = (state.value as? EditMeasureState.Ready) ?: return
        newState.copy(showHelp = event.res).sendToState()
    }

    private fun toggleShowMethod() {
        if (state.value.currentReadOnly) return
        val newState = (state.value as? EditMeasureState.Ready) ?: return

        newState.copy(showMethod = !newState.showMethod).sendToState()
    }

    private fun init(event: EditMeasureEvent.Initialize) {
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
                showMethod = false,
                readOnly = event.initialMeasure != null
            ).sendToState()
        }
    }

    companion object

}
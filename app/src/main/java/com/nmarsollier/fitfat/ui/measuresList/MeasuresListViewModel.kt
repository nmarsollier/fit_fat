package com.nmarsollier.fitfat.ui.measuresList

import androidx.lifecycle.viewModelScope
import com.nmarsollier.fitfat.common.uiUtils.StateViewModel
import com.nmarsollier.fitfat.models.measures.Measure
import com.nmarsollier.fitfat.models.measures.MeasuresRepository
import com.nmarsollier.fitfat.models.userSettings.UserSettings
import com.nmarsollier.fitfat.models.userSettings.UserSettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

sealed interface Destination {
    data class ViewMeasure(val measure: Measure) : Destination
    data object NewMeasure : Destination
}

sealed interface MeasuresListEvent {
    data class Redirect(
        val destination: Destination
    ) : MeasuresListEvent
}

sealed interface MeasuresListState {
    data object Loading : MeasuresListState

    data class Ready(
        val userSettings: UserSettings, val measures: List<Measure>
    ) : MeasuresListState
}

sealed interface MeasuresListAction {
    data class DeleteMeasure(val measure: Measure) : MeasuresListAction
    data object OpenNewMeasure : MeasuresListAction
    data class OpenViewMeasure(val measure: Measure) : MeasuresListAction
}

class MeasuresListViewModel(
    private val measuresRepository: MeasuresRepository,
    private val userSettingsRepository: UserSettingsRepository
) : StateViewModel<MeasuresListState, MeasuresListEvent, MeasuresListAction>(MeasuresListState.Loading) {

    init {
        load()
        viewModelScope.launch(Dispatchers.IO) {
            measuresRepository.updateFlow.collect {
                it?.run {
                    load()
                }
            }
        }
    }

    override fun reduce(action: MeasuresListAction) = when (action) {
        is MeasuresListAction.DeleteMeasure -> deleteMeasure(action)
        MeasuresListAction.OpenNewMeasure -> openNewMeasure()
        is MeasuresListAction.OpenViewMeasure -> openViewMeasure(action)
    }

    private fun load() {
        MeasuresListState.Loading.sendToState()
        viewModelScope.launch(Dispatchers.IO) {
            val userSettings = userSettingsRepository.findCurrent()
            val measures = measuresRepository.findAll()

            MeasuresListState.Ready(
                userSettings = userSettings, measures = measures
            ).sendToState()
        }
    }

    private fun deleteMeasure(event: MeasuresListAction.DeleteMeasure) {
        viewModelScope.launch(Dispatchers.IO) {
            MeasuresListState.Loading.sendToState()
            measuresRepository.findById(event.measure.uid)?.also {
                measuresRepository.delete(it)
            }
        }
    }

    private fun openNewMeasure() {
        MeasuresListEvent.Redirect(Destination.NewMeasure).sendToEvent()
    }

    private fun openViewMeasure(event: MeasuresListAction.OpenViewMeasure) {
        viewModelScope.launch(Dispatchers.IO) {
            MeasuresListEvent.Redirect(Destination.ViewMeasure(event.measure)).sendToEvent()
        }
    }

    companion object

}

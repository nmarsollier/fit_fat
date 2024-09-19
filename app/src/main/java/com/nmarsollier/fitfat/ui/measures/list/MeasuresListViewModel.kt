package com.nmarsollier.fitfat.ui.measures.list

import androidx.compose.runtime.*
import androidx.lifecycle.*
import com.nmarsollier.fitfat.models.measures.*
import com.nmarsollier.fitfat.models.userSettings.*
import com.nmarsollier.fitfat.ui.common.viewModel.*
import kotlinx.coroutines.*

sealed interface Destination {
    @Stable
    data class ViewMeasure(val measure: Measure) : Destination

    @Stable
    data object NewMeasure : Destination
}

sealed interface MeasuresListEvent {
    @Stable
    data class Redirect(
        val destination: Destination
    ) : MeasuresListEvent
}

sealed interface MeasuresListState {
    @Stable
    data object Loading : MeasuresListState

    @Stable
    data class Ready(
        val userSettings: UserSettings, val measures: List<Measure>
    ) : MeasuresListState
}

sealed interface MeasuresListAction {
    @Stable
    data class DeleteMeasure(val measure: Measure) : MeasuresListAction

    @Stable
    data object OpenNewMeasure : MeasuresListAction

    @Stable
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

    override fun reduce(action: MeasuresListAction) {
        when (action) {
            is MeasuresListAction.DeleteMeasure -> deleteMeasure(action)
            MeasuresListAction.OpenNewMeasure -> openNewMeasure()
            is MeasuresListAction.OpenViewMeasure -> openViewMeasure(action)
        }
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

package com.nmarsollier.fitfat.measures.ui.list

import androidx.lifecycle.viewModelScope
import com.nmarsollier.fitfat.common.ui.viewModel.StateViewModel
import com.nmarsollier.fitfat.measures.model.MeasuresRepository
import com.nmarsollier.fitfat.measures.model.db.MeasureData
import com.nmarsollier.fitfat.userSettings.model.UserSettingsRepository
import com.nmarsollier.fitfat.userSettings.model.db.UserSettingsData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

sealed class Destination {
    data class ViewMeasure(val measure: MeasureData) :
        Destination()

    data object NewMeasure : Destination()
}

sealed interface MeasuresListEvent {
    data class Redirect(
        val destination: Destination
    ) : MeasuresListEvent
}

sealed interface MeasuresListState {
    data object Loading : MeasuresListState

    data class Ready(
        val userSettings: UserSettingsData,
        val measures: List<MeasureData>
    ) : MeasuresListState
}

sealed interface MeasuresListAction {
    data object Initialize : MeasuresListAction

    data class DeleteMeasure(val measure: MeasureData) : MeasuresListAction
    data object OpenNewMeasure : MeasuresListAction
    data class OpenViewMeasure(val measure: MeasureData) : MeasuresListAction
}

class MeasuresListViewModel(
    private val measuresRepository: MeasuresRepository,
    private val userSettingsRepository: UserSettingsRepository
) : StateViewModel<MeasuresListState, MeasuresListEvent, MeasuresListAction>(MeasuresListState.Loading) {

    override fun reduce(action: MeasuresListAction) = when (action) {
        is MeasuresListAction.DeleteMeasure -> deleteMeasure(action)
        MeasuresListAction.Initialize -> load()
        MeasuresListAction.OpenNewMeasure -> openNewMeasure()
        is MeasuresListAction.OpenViewMeasure -> openViewMeasure(action)
    }

    private fun load() {
        MeasuresListState.Loading.sendToState()
        viewModelScope.launch(Dispatchers.IO) {
            val userSettings = userSettingsRepository.findCurrent()
            val measures = measuresRepository.findAll()

            MeasuresListState.Ready(
                userSettings = userSettings.value,
                measures = measures.map { it.value }
            ).sendToState()
        }
    }

    private fun deleteMeasure(event: MeasuresListAction.DeleteMeasure) {
        viewModelScope.launch(Dispatchers.IO) {
            MeasuresListState.Loading.sendToState()
            measuresRepository.findById(event.measure.uid)?.also {
                measuresRepository.delete(it)
            }
            load()
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

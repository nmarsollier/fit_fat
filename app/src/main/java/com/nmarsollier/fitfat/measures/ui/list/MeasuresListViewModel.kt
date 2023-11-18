package com.nmarsollier.fitfat.measures.ui.list

import androidx.lifecycle.viewModelScope
import com.nmarsollier.fitfat.common.ui.viewModel.BaseViewModel
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

sealed class MeasuresListState {
    data object Loading : MeasuresListState()

    data class Redirect(
        val destination: Destination
    ) : MeasuresListState()

    data class Ready(
        val userSettings: UserSettingsData,
        val measures: List<MeasureData>
    ) : MeasuresListState()
}

sealed class MeasuresListEvent {
    data object Initialize : MeasuresListEvent()

    data class DeleteMeasure(val measure: MeasureData) : MeasuresListEvent()
    data object OpenNewMeasure : MeasuresListEvent()
    data class OpenViewMeasure(val measure: MeasureData) : MeasuresListEvent()
}

class MeasuresListViewModel(
    private val measuresRepository: MeasuresRepository,
    private val userSettingsRepository: UserSettingsRepository
) : BaseViewModel<MeasuresListState, MeasuresListEvent>(MeasuresListState.Loading) {

    override fun reduce(event: MeasuresListEvent) = when (event) {
        is MeasuresListEvent.DeleteMeasure -> deleteMeasure(event)
        MeasuresListEvent.Initialize -> load()
        MeasuresListEvent.OpenNewMeasure -> openNewMeasure()
        is MeasuresListEvent.OpenViewMeasure -> openViewMeasure(event)
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

    private fun deleteMeasure(event: MeasuresListEvent.DeleteMeasure) {
        viewModelScope.launch(Dispatchers.IO) {
            MeasuresListState.Loading.sendToState()
            measuresRepository.findById(event.measure.uid)?.also {
                measuresRepository.delete(it)
            }
            load()
        }
    }

    private fun openNewMeasure() {
        MeasuresListState.Redirect(Destination.NewMeasure).sendToState()
    }

    private fun openViewMeasure(event: MeasuresListEvent.OpenViewMeasure) {
        viewModelScope.launch(Dispatchers.IO) {
            MeasuresListState.Redirect(Destination.ViewMeasure(event.measure)).sendToState()
        }
    }

    companion object

}

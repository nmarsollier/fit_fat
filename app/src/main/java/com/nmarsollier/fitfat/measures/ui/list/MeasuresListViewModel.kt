package com.nmarsollier.fitfat.measures.ui.list

import androidx.lifecycle.viewModelScope
import com.nmarsollier.fitfat.measures.model.db.MeasureData
import com.nmarsollier.fitfat.measures.model.MeasuresRepository
import com.nmarsollier.fitfat.userSettings.model.db.UserSettingsData
import com.nmarsollier.fitfat.userSettings.model.UserSettingsRepository
import com.nmarsollier.fitfat.common.ui.viewModel.BaseViewModel
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

interface MeasuresListReducer {
    fun load()
    fun deleteMeasure(measure: MeasureData)
    fun openNewMeasure()
    fun openViewMeasure(measure: MeasureData)
}

class MeasuresListViewModel(
    private val measuresRepository: MeasuresRepository,
    private val userSettingsRepository: UserSettingsRepository
) : BaseViewModel<MeasuresListState>(MeasuresListState.Loading), MeasuresListReducer {

    override fun load() {
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

    override fun deleteMeasure(measure: MeasureData) {
        viewModelScope.launch(Dispatchers.IO) {
            MeasuresListState.Loading.sendToState()
            measuresRepository.findById(measure.uid)?.also {
                measuresRepository.delete(it)
            }
            load()
        }
    }

    override fun openNewMeasure() {
        MeasuresListState.Redirect(Destination.NewMeasure).sendToState()
    }

    override fun openViewMeasure(measure: MeasureData) {
        viewModelScope.launch(Dispatchers.IO) {
            MeasuresListState.Redirect(Destination.ViewMeasure(measure)).sendToState()
        }
    }

    companion object
}

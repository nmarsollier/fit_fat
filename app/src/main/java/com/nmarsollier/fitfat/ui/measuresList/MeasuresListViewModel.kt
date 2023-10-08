package com.nmarsollier.fitfat.ui.measuresList

import androidx.lifecycle.viewModelScope
import com.nmarsollier.fitfat.model.measures.Measure
import com.nmarsollier.fitfat.model.measures.MeasuresRepository
import com.nmarsollier.fitfat.model.userSettings.UserSettingsEntity
import com.nmarsollier.fitfat.model.userSettings.UserSettingsRepository
import com.nmarsollier.fitfat.utils.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class Destination {
    data class ViewMeasure(val measure: Measure) : Destination()
    data object NewMeasure : Destination()
}

sealed class MeasuresListState {
    data object Loading : MeasuresListState()

    data class Redirect(
        val destination: Destination
    ) : MeasuresListState()

    data class Ready(
        val userSettingsEntity: UserSettingsEntity, val measures: List<Measure>
    ) : MeasuresListState()
}

interface MeasuresListReducer {
    fun load()
    fun deleteMeasure(measure: Measure)
    fun openNewMeasure()
    fun openViewMeasure(measure: Measure)
}

class MeasuresListViewModel(
    val measuresRepository: MeasuresRepository,
    val userSettingsRepository: UserSettingsRepository
) : BaseViewModel<MeasuresListState>(MeasuresListState.Loading), MeasuresListReducer {

    override fun load() {
        mutableState.update { MeasuresListState.Loading }
        viewModelScope.launch(Dispatchers.IO) {
            val userSettings = userSettingsRepository.load()
            val measures = measuresRepository.loadAll()

            mutableState.update {
                MeasuresListState.Ready(userSettingsEntity = userSettings, measures = measures)
            }
        }
    }

    override fun deleteMeasure(measure: Measure) {
        viewModelScope.launch(Dispatchers.IO) {
            mutableState.update { MeasuresListState.Loading }
            measuresRepository.delete(measure)
            load()
        }
    }

    override fun openNewMeasure() {
        mutableState.update {
            MeasuresListState.Redirect(Destination.NewMeasure)
        }
    }

    override fun openViewMeasure(measure: Measure) {
        mutableState.update {
            MeasuresListState.Redirect(Destination.ViewMeasure(measure))
        }
    }

    companion object
}

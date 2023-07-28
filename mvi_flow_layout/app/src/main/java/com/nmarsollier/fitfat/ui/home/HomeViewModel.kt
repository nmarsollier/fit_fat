package com.nmarsollier.fitfat.ui.home

import androidx.lifecycle.viewModelScope
import com.nmarsollier.fitfat.model.measures.Measure
import com.nmarsollier.fitfat.model.measures.MeasuresRepository
import com.nmarsollier.fitfat.model.userSettings.UserSettings
import com.nmarsollier.fitfat.model.userSettings.UserSettingsRepository
import com.nmarsollier.fitfat.utils.BaseViewModel
import com.nmarsollier.fitfat.utils.collectOnce
import com.nmarsollier.fitfat.utils.ifNotNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class HomeState {
    object Loading : HomeState()

    data class Ready(
        val userSettings: UserSettings, val measures: List<Measure>
    ) : HomeState()
}

class HomeViewModel : BaseViewModel<HomeState>(HomeState.Loading) {
    private var userSettings: UserSettings? = null
    private var measures: List<Measure>? = null

    fun load() {
        viewModelScope.launch(Dispatchers.IO) {
            UserSettingsRepository.load().collectOnce {
                userSettings = it
                updateState()
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            MeasuresRepository.loadAll().collectOnce {
                measures = it
                updateState()
            }
        }
    }

    fun deleteMeasure(measure: Measure) {
        mutableState.update { HomeState.Loading }
        viewModelScope.launch(Dispatchers.IO) {
            MeasuresRepository.delete(measure)
            MeasuresRepository.loadAll()
        }
    }

    private fun updateState() = viewModelScope.launch(Dispatchers.IO) {
        ifNotNull(measures, userSettings) { measures, userSettings ->
            mutableState.update {
                HomeState.Ready(
                    userSettings = userSettings, measures = measures
                )
            }
            MeasuresRepository.loadAll()
        }
    }
}
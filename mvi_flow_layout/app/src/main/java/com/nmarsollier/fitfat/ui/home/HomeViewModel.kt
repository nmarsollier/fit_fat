package com.nmarsollier.fitfat.ui.home

import androidx.lifecycle.viewModelScope
import com.nmarsollier.fitfat.model.measures.Measure
import com.nmarsollier.fitfat.model.measures.MeasuresRepository
import com.nmarsollier.fitfat.model.userSettings.UserSettings
import com.nmarsollier.fitfat.model.userSettings.UserSettingsRepository
import com.nmarsollier.fitfat.utils.BaseViewModel
import com.nmarsollier.fitfat.utils.ifNotNull
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class HomeState {
    object Loading : HomeState()

    data class Ready(
        val userSettings: UserSettings, val measures: List<Measure>
    ) : HomeState()
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    val measuresRepository: MeasuresRepository,
    val userSettingsRepository: UserSettingsRepository
) : BaseViewModel<HomeState>(HomeState.Loading) {
    fun load() {
        viewModelScope.launch(Dispatchers.IO) {
            val userSettings = userSettingsRepository.load()
            val measures = measuresRepository.loadAll()

            updateState(userSettings, measures)
        }
    }

    fun deleteMeasure(measure: Measure) {
        mutableState.update { HomeState.Loading }
        viewModelScope.launch(Dispatchers.IO) {
            measuresRepository.delete(measure)
            measuresRepository.loadAll()
        }
    }

    private fun updateState(userSettings: UserSettings, measures: List<Measure>) =
        viewModelScope.launch(Dispatchers.IO) {
            ifNotNull(measures, userSettings) { measures, userSettings ->
                mutableState.update {
                    HomeState.Ready(
                        userSettings = userSettings, measures = measures
                    )
                }
            }
        }
}
package com.nmarsollier.fitfat.ui.home

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.nmarsollier.fitfat.model.measures.Measure
import com.nmarsollier.fitfat.model.measures.MeasuresRepository
import com.nmarsollier.fitfat.model.userSettings.UserSettings
import com.nmarsollier.fitfat.model.userSettings.UserSettingsRepository
import com.nmarsollier.fitfat.ui.utils.BaseViewModel
import com.nmarsollier.fitfat.utils.ifNotNull
import com.nmarsollier.fitfat.utils.runInForeground
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class HomeState {
    object Initial : HomeState()
    object Loading : HomeState()

    data class Ready(
        val userSettings: UserSettings,
        val measures: List<Measure>
    ) : HomeState()
}

class HomeViewModel : BaseViewModel<HomeState>(HomeState.Initial) {
    private var userSettings: UserSettings? = null
    private var measures: List<Measure>? = null

    fun init(context: Context) = viewModelScope.launch {
        mutableState.update { HomeState.Loading }

        viewModelScope.launch(Dispatchers.IO) {
            UserSettingsRepository.load(context).collect {
                userSettings = it
                updateState()
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            MeasuresRepository.loadAll(context).collect {
                measures = it
                updateState()
            }
        }
    }

    fun deleteMeasure(context: Context, measure: Measure) = viewModelScope.launch {
        mutableState.update { HomeState.Loading }
        MeasuresRepository.delete(context, measure)
    }

    private fun updateState() = runInForeground {
        ifNotNull(measures, userSettings) { measures, userSettings ->
            mutableState.update {
                HomeState.Ready(
                    userSettings = userSettings,
                    measures = measures
                )
            }
        }
    }
}
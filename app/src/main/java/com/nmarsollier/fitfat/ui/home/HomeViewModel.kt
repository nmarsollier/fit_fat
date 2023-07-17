package com.nmarsollier.fitfat.ui.home

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.nmarsollier.fitfat.model.Measure
import com.nmarsollier.fitfat.model.MeasuresRepository
import com.nmarsollier.fitfat.model.UserSettings
import com.nmarsollier.fitfat.model.UserSettingsRepository
import com.nmarsollier.fitfat.ui.utils.BaseViewModel
import com.nmarsollier.fitfat.utils.ifNotNull
import com.nmarsollier.fitfat.utils.runInForeground
import kotlinx.coroutines.flow.firstOrNull
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

    fun loadSettings(context: Context) = viewModelScope.launch {
        mutableState.update { HomeState.Loading }

        UserSettingsRepository.load(context).firstOrNull {
            userSettings = it
            updateState()
            true
        }

        MeasuresRepository.loadAll(context).firstOrNull {
            measures = it
            updateState()
            true
        }
    }

    fun deleteMeasure(context: Context, measure: Measure) = viewModelScope.launch {
        mutableState.update { HomeState.Loading }
        MeasuresRepository.delete(context, measure).firstOrNull {
            loadSettings(context)
            true
        }
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
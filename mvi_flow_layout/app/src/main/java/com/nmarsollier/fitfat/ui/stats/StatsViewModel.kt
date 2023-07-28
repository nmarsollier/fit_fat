package com.nmarsollier.fitfat.ui.stats

import androidx.lifecycle.viewModelScope
import com.nmarsollier.fitfat.model.measures.Measure
import com.nmarsollier.fitfat.model.measures.MeasureMethod
import com.nmarsollier.fitfat.model.measures.MeasuresRepository
import com.nmarsollier.fitfat.model.userSettings.UserSettings
import com.nmarsollier.fitfat.model.userSettings.UserSettingsRepository
import com.nmarsollier.fitfat.utils.BaseViewModel
import com.nmarsollier.fitfat.utils.collectOnce
import com.nmarsollier.fitfat.utils.ifNotNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class StatsState(
    val selectedMethod: MeasureMethod
) {
    data class Loading(
        private val method: MeasureMethod
    ) : StatsState(method)

    data class Ready(
        private val method: MeasureMethod,
        val userSettings: UserSettings,
        val measures: List<Measure>
    ) : StatsState(method)
}

class StatsViewModel : BaseViewModel<StatsState>(StatsState.Loading(MeasureMethod.WEIGHT_ONLY)) {
    private var userSettings: UserSettings? = null
    private var measures: List<Measure>? = null

    private val measureMethod: MeasureMethod
        get() = state.value.selectedMethod

    fun init() {
        mutableState.update {
            StatsState.Loading(measureMethod)
        }

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

    fun updateMethod(selectedMethod: MeasureMethod) {
        mutableState.update {
            when (val value = state.value) {
                is StatsState.Loading -> value.copy(method = selectedMethod)
                is StatsState.Ready -> value.copy(method = selectedMethod)
            }
        }
    }

    private fun updateState() = viewModelScope.launch {
        ifNotNull(
            userSettings, measures
        ) { userSettings, measures ->
            mutableState.update {
                StatsState.Ready(
                    method = measureMethod, userSettings = userSettings, measures = measures
                )
            }
        }
    }
}
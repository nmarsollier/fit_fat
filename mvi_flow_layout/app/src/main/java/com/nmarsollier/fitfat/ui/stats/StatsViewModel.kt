package com.nmarsollier.fitfat.ui.stats

import androidx.lifecycle.viewModelScope
import com.nmarsollier.fitfat.model.measures.Measure
import com.nmarsollier.fitfat.model.measures.MeasureMethod
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

@HiltViewModel
class StatsViewModel @Inject constructor(
    val userSettingsRepository: UserSettingsRepository,
    val measuresRepository: MeasuresRepository
) : BaseViewModel<StatsState>(StatsState.Loading(MeasureMethod.WEIGHT_ONLY)) {
    private val measureMethod: MeasureMethod
        get() = state.value.selectedMethod

    fun init() {
        mutableState.update {
            StatsState.Loading(measureMethod)
        }

        viewModelScope.launch(Dispatchers.IO) {
            val userSettings = userSettingsRepository.load()
            val measures = measuresRepository.loadAll()
            updateState(userSettings, measures)
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

    private fun updateState(userSettings: UserSettings, measures: List<Measure>) =
        viewModelScope.launch {
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
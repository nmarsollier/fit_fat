package com.nmarsollier.fitfat.ui.stats

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.nmarsollier.fitfat.model.measures.Measure
import com.nmarsollier.fitfat.model.measures.MeasureMethod
import com.nmarsollier.fitfat.model.measures.MeasuresRepository
import com.nmarsollier.fitfat.model.userSettings.UserSettings
import com.nmarsollier.fitfat.model.userSettings.UserSettingsRepository
import com.nmarsollier.fitfat.ui.utils.BaseViewModel
import com.nmarsollier.fitfat.utils.ifNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class StatsState(
    val selectedMethod: MeasureMethod
) {
    data class Initial(
        private val method: MeasureMethod
    ) : StatsState(method)

    data class Loading(
        private val method: MeasureMethod
    ) : StatsState(method)

    data class Ready(
        private val method: MeasureMethod,
        val userSettings: UserSettings,
        val measures: List<Measure>
    ) : StatsState(method)
}

class StatsViewModel : BaseViewModel<StatsState>(StatsState.Initial(MeasureMethod.WEIGHT_ONLY)) {
    private var userSettings: UserSettings? = null
    private var measures: List<Measure>? = null

    private val measureMethod: MeasureMethod
        get() = state.value.selectedMethod

    fun load(
        context: Context
    ) = viewModelScope.launch {

        mutableState.update {
            StatsState.Loading(measureMethod)
        }

        UserSettingsRepository.load(context).collect {
            userSettings = it
            updateState()
        }

        MeasuresRepository.loadAll(context).collect {
            measures = it
            updateState()
        }
    }

    fun updateMethod(selectedMethod: MeasureMethod) {
        mutableState.update {
            when (val value = state.value) {
                is StatsState.Initial -> value.copy(method = selectedMethod)
                is StatsState.Loading -> value.copy(method = selectedMethod)
                is StatsState.Ready -> value.copy(method = selectedMethod)
            }
        }
    }

    private fun updateState() = viewModelScope.launch {
        ifNotNull(
            userSettings,
            measures
        ) { userSettings, measures ->
            mutableState.update {
                StatsState.Ready(
                    method = measureMethod,
                    userSettings = userSettings,
                    measures = measures
                )
            }
        }
    }
}
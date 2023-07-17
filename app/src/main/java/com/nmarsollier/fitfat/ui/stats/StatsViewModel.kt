package com.nmarsollier.fitfat.ui.stats

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.nmarsollier.fitfat.model.*
import com.nmarsollier.fitfat.ui.utils.BaseViewModel
import com.nmarsollier.fitfat.utils.ifNotNull
import kotlinx.coroutines.flow.firstOrNull
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
        get() = state.value?.selectedMethod ?: MeasureMethod.WEIGHT_ONLY

    fun load(
        context: Context
    ) = viewModelScope.launch {

        state.emit(StatsState.Loading(measureMethod))

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

    fun updateMethod(selectedMethod: MeasureMethod) {
        state.emit(
            when (val value = state.value) {
                is StatsState.Initial -> value.copy(method = selectedMethod)
                is StatsState.Loading -> value.copy(method = selectedMethod)
                is StatsState.Ready -> value.copy(method = selectedMethod)
                null -> StatsState.Initial(selectedMethod)
            }
        )
    }

    private fun updateState() = viewModelScope.launch {
        ifNotNull(
            userSettings,
            measures
        ) { userSettings, measures ->
            state.emit(
                StatsState.Ready(
                    method = measureMethod,
                    userSettings = userSettings,
                    measures = measures
                )
            )
        }
    }
}
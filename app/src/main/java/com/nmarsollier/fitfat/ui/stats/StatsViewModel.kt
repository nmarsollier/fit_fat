package com.nmarsollier.fitfat.ui.stats

import androidx.compose.runtime.*
import androidx.lifecycle.*
import com.nmarsollier.fitfat.models.measures.*
import com.nmarsollier.fitfat.models.measures.db.*
import com.nmarsollier.fitfat.models.userSettings.*
import com.nmarsollier.fitfat.ui.common.viewModel.*
import kotlinx.coroutines.*

@Stable
sealed class StatsState(
    val selectedMethod: MeasureMethod
) {
    @Stable
    data class Loading(
        private val method: MeasureMethod
    ) : StatsState(method)

    @Stable
    data class Ready(
        private val method: MeasureMethod,
        val userSettings: UserSettings,
        val measures: List<Measure>,
        val showMethod: Boolean
    ) : StatsState(method)
}

@Stable
sealed interface StatsAction {
    @Stable
    data class UpdateMethod(val selectedMethod: MeasureMethod) : StatsAction

    @Stable
    data object ToggleShowMethod : StatsAction
}

class StatsViewModel(
    private val userSettingsRepository: UserSettingsRepository,
    private val measuresRepository: MeasuresRepository
) : StateViewModel<StatsState, Void, StatsAction>(
    StatsState.Loading(
        MeasureMethod.WEIGHT_ONLY
    )
) {
    init {
        init()

        viewModelScope.launch(Dispatchers.IO) {
            userSettingsRepository.updateFlow.collect {
                it?.run {
                    init()
                }
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            measuresRepository.updateFlow.collect {
                it?.run {
                    init()
                }
            }
        }
    }

    private val measureMethod: MeasureMethod
        get() = state.value.selectedMethod


    override fun reduce(action: StatsAction) {
        when (action) {
            StatsAction.ToggleShowMethod -> toggleShowMethod()
            is StatsAction.UpdateMethod -> updateMethod(action)
        }
    }

    private fun init() {
        StatsState.Loading(measureMethod).sendToState()

        viewModelScope.launch(Dispatchers.IO) {
            val userSettings = userSettingsRepository.findCurrent()
            val measures = measuresRepository.findAll()

            StatsState.Ready(
                method = measureMethod,
                userSettings = userSettings,
                measures = measures,
                showMethod = false
            ).sendToState()
        }
    }

    private fun updateMethod(event: StatsAction.UpdateMethod) {
        when (val value = state.value) {
            is StatsState.Loading -> value.copy(method = event.selectedMethod)
            is StatsState.Ready -> value.copy(method = event.selectedMethod, showMethod = false)
        }.sendToState()
    }

    private fun toggleShowMethod() {
        val st = (state.value as? StatsState.Ready) ?: return
        st.copy(showMethod = !st.showMethod).sendToState()
    }

    companion object
}
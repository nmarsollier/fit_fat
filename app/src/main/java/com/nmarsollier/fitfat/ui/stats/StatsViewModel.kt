package com.nmarsollier.fitfat.ui.stats

import androidx.lifecycle.viewModelScope
import com.nmarsollier.fitfat.common.uiUtils.StateViewModel
import com.nmarsollier.fitfat.models.measures.Measure
import com.nmarsollier.fitfat.models.measures.MeasuresRepository
import com.nmarsollier.fitfat.models.measures.db.MeasureMethod
import com.nmarsollier.fitfat.models.userSettings.UserSettings
import com.nmarsollier.fitfat.models.userSettings.UserSettingsRepository
import kotlinx.coroutines.Dispatchers
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
        val measures: List<Measure>,
        val showMethod: Boolean
    ) : StatsState(method)
}

sealed interface StatsEvent

sealed interface StatsAction {
    data class UpdateMethod(val selectedMethod: MeasureMethod) : StatsAction
    data object ToggleShowMethod : StatsAction
}

class StatsViewModel(
    private val userSettingsRepository: UserSettingsRepository,
    private val measuresRepository: MeasuresRepository
) : StateViewModel<StatsState, StatsEvent, StatsAction>(
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


    override fun reduce(action: StatsAction) = when (action) {
        StatsAction.ToggleShowMethod -> toggleShowMethod()
        is StatsAction.UpdateMethod -> updateMethod(action)
    }

    private fun init() {
        StatsState.Loading(measureMethod).sendToState()

        viewModelScope.launch(Dispatchers.IO) {
            val userSettings = userSettingsRepository.findCurrent()
            val measures = measuresRepository.findAll()

            viewModelScope.launch {
                StatsState.Ready(
                    method = measureMethod,
                    userSettings = userSettings,
                    measures = measures,
                    showMethod = false
                ).sendToState()
            }
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
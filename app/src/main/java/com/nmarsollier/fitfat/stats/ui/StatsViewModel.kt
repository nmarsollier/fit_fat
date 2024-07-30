package com.nmarsollier.fitfat.stats.ui

import androidx.lifecycle.viewModelScope
import com.nmarsollier.fitfat.common.ui.viewModel.StateViewModel
import com.nmarsollier.fitfat.measures.model.MeasuresRepository
import com.nmarsollier.fitfat.measures.model.db.MeasureData
import com.nmarsollier.fitfat.measures.model.db.MeasureMethod
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
        val userSettings: com.nmarsollier.fitfat.userSettings.model.db.UserSettingsData,
        val measures: List<MeasureData>,
        val showMethod: Boolean
    ) : StatsState(method)
}

sealed interface StatsEvent

sealed interface StatsAction {
    data object Initialize : StatsAction
    data class UpdateMethod(val selectedMethod: MeasureMethod) : StatsAction
    data object ToggleShowMethod : StatsAction
}

class StatsViewModel(
    private val userSettingsRepository: com.nmarsollier.fitfat.userSettings.model.UserSettingsRepository,
    private val measuresRepository: MeasuresRepository
) : StateViewModel<StatsState, StatsEvent, StatsAction>(
    StatsState.Loading(
        MeasureMethod.WEIGHT_ONLY
    )
) {
    private val measureMethod: MeasureMethod
        get() = state.value.selectedMethod


    override fun reduce(action: StatsAction) = when (action) {
        StatsAction.Initialize -> init()
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
                    userSettings = userSettings.value,
                    measures = measures.map { it.value },
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
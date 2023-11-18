package com.nmarsollier.fitfat.stats.ui

import androidx.lifecycle.viewModelScope
import com.nmarsollier.fitfat.measures.model.db.MeasureData
import com.nmarsollier.fitfat.measures.model.db.MeasureMethod
import com.nmarsollier.fitfat.measures.model.MeasuresRepository
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

sealed class StatsEvent {
    data object Initialize : StatsEvent()
    data class UpdateMethod(val selectedMethod: MeasureMethod) : StatsEvent()
    data object ToggleShowMethod : StatsEvent()
}

class StatsView(
    private val userSettingsRepository: com.nmarsollier.fitfat.userSettings.model.UserSettingsRepository,
    private val measuresRepository: MeasuresRepository
) : com.nmarsollier.fitfat.common.ui.viewModel.BaseView<StatsState, StatsEvent>(
    StatsState.Loading(
        MeasureMethod.WEIGHT_ONLY
    )
) {
    private val measureMethod: MeasureMethod
        get() = state.value.selectedMethod



    override fun reduce(event: StatsEvent) = when (event) {
        StatsEvent.Initialize -> init()
        StatsEvent.ToggleShowMethod -> toggleShowMethod()
        is StatsEvent.UpdateMethod -> updateMethod(event)
    }

    private fun init() {
        StatsState.Loading(measureMethod).toState()

        viewModelScope.launch(Dispatchers.IO) {
            val userSettings = userSettingsRepository.findCurrent()
            val measures = measuresRepository.findAll()

            viewModelScope.launch {
                StatsState.Ready(
                    method = measureMethod,
                    userSettings = userSettings.value,
                    measures = measures.map { it.value },
                    showMethod = false
                ).toState()
            }
        }
    }

    private fun updateMethod(event: StatsEvent.UpdateMethod) {
        when (val value = state.value) {
            is StatsState.Loading -> value.copy(method = event.selectedMethod)
            is StatsState.Ready -> value.copy(method = event.selectedMethod, showMethod = false)
        }.toState()
    }

    private fun toggleShowMethod() {
        val st = (state.value as? StatsState.Ready) ?: return
        st.copy(showMethod = !st.showMethod).toState()
    }

    companion object
}
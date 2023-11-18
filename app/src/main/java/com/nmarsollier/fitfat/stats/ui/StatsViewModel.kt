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

interface StatsReducer {
    fun init()
    fun updateMethod(selectedMethod: MeasureMethod)
    fun toggleShowMethod()
}

class StatsViewModel(
    private val userSettingsRepository: com.nmarsollier.fitfat.userSettings.model.UserSettingsRepository,
    private val measuresRepository: MeasuresRepository
) : com.nmarsollier.fitfat.common.ui.viewModel.BaseViewModel<StatsState>(
    StatsState.Loading(
        MeasureMethod.WEIGHT_ONLY
    )
), StatsReducer {
    private val measureMethod: MeasureMethod
        get() = state.value.selectedMethod

    override fun init() {
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

    override fun updateMethod(selectedMethod: MeasureMethod) {
        when (val value = state.value) {
            is StatsState.Loading -> value.copy(method = selectedMethod)
            is StatsState.Ready -> value.copy(method = selectedMethod, showMethod = false)
        }.sendToState()
    }

    override fun toggleShowMethod() {
        val st = (state.value as? StatsState.Ready) ?: return
        st.copy(showMethod = !st.showMethod).sendToState()
    }

    companion object
}
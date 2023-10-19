package com.nmarsollier.fitfat.stats.ui.stats

import androidx.lifecycle.viewModelScope
import com.nmarsollier.fitfat.measures.model.db.MeasureData
import com.nmarsollier.fitfat.measures.model.db.MeasureMethod
import com.nmarsollier.fitfat.measures.model.MeasuresRepository
import com.nmarsollier.fitfat.userSettings.model.db.UserSettingsData
import com.nmarsollier.fitfat.userSettings.model.UserSettingsRepository
import com.nmarsollier.fitfat.utils.ui.viewModel.BaseViewModel
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
        val userSettings: UserSettingsData,
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
    private val userSettingsRepository: UserSettingsRepository,
    private val measuresRepository: MeasuresRepository
) : BaseViewModel<StatsState>(StatsState.Loading(MeasureMethod.WEIGHT_ONLY)), StatsReducer {
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
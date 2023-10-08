package com.nmarsollier.fitfat.ui.stats

import androidx.lifecycle.viewModelScope
import com.nmarsollier.fitfat.model.measures.Measure
import com.nmarsollier.fitfat.model.measures.MeasureMethod
import com.nmarsollier.fitfat.model.measures.MeasuresRepository
import com.nmarsollier.fitfat.model.userSettings.UserSettingsEntity
import com.nmarsollier.fitfat.model.userSettings.UserSettingsRepository
import com.nmarsollier.fitfat.utils.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.getAndUpdate
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
        val userSettingsEntity: UserSettingsEntity,
        val measures: List<Measure>,
        val showMethod: Boolean
    ) : StatsState(method)
}

interface StatsReducer {
    fun init()
    fun updateMethod(selectedMethod: MeasureMethod)
    fun toggleShowMethod()
}

class StatsViewModel(
    val userSettingsRepository: UserSettingsRepository,
    private val measuresRepository: MeasuresRepository
) : BaseViewModel<StatsState>(StatsState.Loading(MeasureMethod.WEIGHT_ONLY)), StatsReducer {
    private val measureMethod: MeasureMethod
        get() = state.value.selectedMethod

    override fun init() {
        mutableState.update {
            StatsState.Loading(measureMethod)
        }

        viewModelScope.launch(Dispatchers.IO) {
            val userSettings = userSettingsRepository.load()
            val measures = measuresRepository.loadAll()

            viewModelScope.launch {
                mutableState.update {
                    StatsState.Ready(
                        method = measureMethod,
                        userSettingsEntity = userSettings,
                        measures = measures,
                        showMethod = false
                    )
                }
            }
        }
    }

    override fun updateMethod(selectedMethod: MeasureMethod) {
        mutableState.update {
            when (val value = state.value) {
                is StatsState.Loading -> value.copy(method = selectedMethod)
                is StatsState.Ready -> value.copy(method = selectedMethod, showMethod = false)
            }
        }
    }

    override fun toggleShowMethod() {
        mutableState.getAndUpdate {
            (it as? StatsState.Ready)?.let { _ ->
                it.copy(showMethod = !it.showMethod)
            } ?: it
        }
    }

    companion object
}
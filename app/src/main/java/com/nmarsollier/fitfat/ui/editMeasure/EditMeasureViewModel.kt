package com.nmarsollier.fitfat.ui.editMeasure

import androidx.lifecycle.viewModelScope
import com.nmarsollier.fitfat.model.measures.Measure
import com.nmarsollier.fitfat.model.measures.MeasureMethod
import com.nmarsollier.fitfat.model.measures.MeasureValue
import com.nmarsollier.fitfat.model.measures.MeasuresRepository
import com.nmarsollier.fitfat.model.userSettings.UserSettingsEntity
import com.nmarsollier.fitfat.model.userSettings.UserSettingsRepository
import com.nmarsollier.fitfat.utils.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date
import java.util.UUID

sealed class EditMeasureState {
    data class Loading(
        val measure: Measure?, val readOnly: Boolean
    ) : EditMeasureState()

    data object Invalid : EditMeasureState()
    data object Close : EditMeasureState()

    data class Ready(
        val userSettingsEntity: UserSettingsEntity,
        val measure: Measure,
        val showHelp: Int?,
        val showMethod: Boolean,
        val readOnly: Boolean
    ) : EditMeasureState()

    val currentReadOnly: Boolean
        get() = when (this) {
            is Loading -> readOnly
            is Ready -> readOnly
            else -> true
        }
}

interface EditMeasureReducer {
    fun saveMeasure()
    fun updateDate(time: Date)
    fun updateMeasureMethod(measureMethod: MeasureMethod)
    fun updateMeasureValue(measureValue: MeasureValue, value: Number)
    fun close()
    fun toggleHelp(res: Int?)
    fun toggleShowMethod()
    fun init(initialMeasure: Measure?)
}

class EditMeasureViewModel(
    private val measuresRepository: MeasuresRepository,
    private val userSettingsRepository: UserSettingsRepository
) : BaseViewModel<EditMeasureState>(EditMeasureState.Loading(null, false)), EditMeasureReducer {
    private val currentMeasure: Measure?
        get() = when (val st = state.value) {
            is EditMeasureState.Loading -> st.measure
            is EditMeasureState.Ready -> st.measure
            else -> null
        }

    override fun saveMeasure() {
        viewModelScope.launch {
            val currentState = state.value
            val measure = currentMeasure ?: return@launch
            val readOnly = state.value.currentReadOnly
            val userSettings =
                (currentState as? EditMeasureState.Ready)?.userSettingsEntity ?: return@launch

            mutableState.update {
                EditMeasureState.Loading(null, readOnly)
            }

            if (measure.isValid()) {
                withContext(Dispatchers.IO) {
                    measuresRepository.insert(measure)
                    if (measure.bodyWeight > 0) {
                        userSettings.weight = measure.bodyWeight
                        userSettingsRepository.update(userSettings)
                    }
                }

                mutableState.update { EditMeasureState.Close }
            } else {
                mutableState.update { EditMeasureState.Invalid }
                mutableState.update { currentState }
            }
        }
    }

    override fun updateDate(time: Date) {
        if (state.value.currentReadOnly) return

        (state.value as? EditMeasureState.Ready)?.let { newState ->
            mutableState.update {
                newState.copy(
                    measure = newState.measure.copy(
                        date = time
                    )
                )
            }
        }
    }

    override fun updateMeasureMethod(measureMethod: MeasureMethod) {
        if (state.value.currentReadOnly) return

        mutableState.getAndUpdate {
            (it as? EditMeasureState.Ready)?.let { _ ->
                it.copy(
                    measure = it.measure.copy(
                        measureMethod = measureMethod
                    ), showMethod = false
                )
            } ?: it
        }
    }

    override fun updateMeasureValue(measureValue: MeasureValue, value: Number) {
        if (state.value.currentReadOnly) return

        mutableState.getAndUpdate {
            (it as? EditMeasureState.Ready)?.let { _ ->
                it.copy(measure = it.measure.copy().apply {
                    setValueForMethod(measureValue, value)
                })
            } ?: it
        }
    }

    override fun close() {
        mutableState.update {
            EditMeasureState.Close
        }
    }

    override fun toggleHelp(res: Int?) {
        mutableState.getAndUpdate {
            (it as? EditMeasureState.Ready)?.let { _ ->
                it.copy(showHelp = res)
            } ?: it
        }
    }

    override fun toggleShowMethod() {
        if (state.value.currentReadOnly) return

        mutableState.getAndUpdate {
            (it as? EditMeasureState.Ready)?.let { _ ->
                it.copy(showMethod = !it.showMethod)
            } ?: it
        }
    }

    override fun init(initialMeasure: Measure?) {
        mutableState.update {
            EditMeasureState.Loading(
                initialMeasure, initialMeasure != null
            )
        }

        viewModelScope.launch(Dispatchers.IO) {
            val userSettings = userSettingsRepository.load()
            val lastMeasure = measuresRepository.findLast()

            mutableState.update {
                EditMeasureState.Ready(
                    userSettingsEntity = userSettings,
                    measure = initialMeasure ?: lastMeasure?.copy(
                        uid = UUID.randomUUID().toString(), date = Date()
                    ) ?: Measure.newMeasure(),
                    showHelp = null,
                    showMethod = false,
                    readOnly = initialMeasure != null
                )
            }
        }
    }

    companion object
}
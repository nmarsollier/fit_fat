package com.nmarsollier.fitfat.measures.ui.editMeasure

import androidx.lifecycle.viewModelScope
import com.nmarsollier.fitfat.measures.model.Measure
import com.nmarsollier.fitfat.measures.model.db.MeasureData
import com.nmarsollier.fitfat.measures.model.db.MeasureMethod
import com.nmarsollier.fitfat.measures.model.db.MeasureValue
import com.nmarsollier.fitfat.measures.model.MeasuresRepository
import com.nmarsollier.fitfat.userSettings.model.UserSettings
import com.nmarsollier.fitfat.userSettings.model.db.UserSettingsData
import com.nmarsollier.fitfat.userSettings.model.UserSettingsRepository
import com.nmarsollier.fitfat.utils.ui.viewModel.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date

sealed class EditMeasureState {
    data class Loading(
        val measure: MeasureData?, val readOnly: Boolean
    ) : EditMeasureState()

    data object Invalid : EditMeasureState()
    data object Close : EditMeasureState()

    data class Ready(
        val userSettings: UserSettingsData,
        val measure: MeasureData,
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
    fun updateMeasureValue(
        measureValue: MeasureValue,
        value: Number
    )

    fun close()
    fun toggleHelp(res: Int?)
    fun toggleShowMethod()
    fun init(initialMeasure: MeasureData?)
}

class EditMeasureViewModel(
    private val measuresRepository: MeasuresRepository,
    private val userSettingsRepository: UserSettingsRepository
) : BaseViewModel<EditMeasureState>(EditMeasureState.Loading(null, false)), EditMeasureReducer {
    private var userSettings: UserSettings? = null
    private var measure: Measure? = null

    override fun saveMeasure() {
        viewModelScope.launch {
            val currentState = state.value
            val measure = measure ?: return@launch
            val readOnly = state.value.currentReadOnly
            val userSettings = userSettings ?: return@launch

            EditMeasureState.Loading(null, readOnly).sendToState()

            if (measure.isValid()) {
                withContext(Dispatchers.IO) {
                    userSettings.updateWeight(measure.value.bodyWeight)

                    measure.updateCloudSync(false)
                    measuresRepository.update(measure)

                    if (measure.value.bodyWeight > 0) {
                        userSettingsRepository.update(userSettings)
                    }
                }

                EditMeasureState.Close.sendToState()
            } else {
                EditMeasureState.Invalid.sendToState()
                currentState.sendToState()
            }
        }
    }

    override fun updateDate(time: Date) {
        if (state.value.currentReadOnly) return
        val measure = measure ?: return

        (state.value as? EditMeasureState.Ready)?.apply {
            measure.updateDate(time)
            copy(
                measure = measure.value
            ).sendToState()
        }
    }

    override fun updateMeasureMethod(measureMethod: MeasureMethod) {
        if (state.value.currentReadOnly) return
        val measure = measure ?: return

        (state.value as? EditMeasureState.Ready)?.apply {
            measure.updateMeasureMethod(measureMethod)
            copy(
                measure = measure.value,
                showMethod = false
            ).sendToState()
        }
    }

    override fun updateMeasureValue(
        measureValue: MeasureValue,
        value: Number
    ) {
        if (state.value.currentReadOnly) return
        val measure = measure ?: return

        (state.value as? EditMeasureState.Ready)?.apply {
            measure.updateMethodValue(measureValue, value)
            copy(
                measure = measure.value,
                showMethod = false
            ).sendToState()
        }
    }

    override fun close() {
        EditMeasureState.Close.sendToState()
    }

    override fun toggleHelp(res: Int?) {
        val newState = (state.value as? EditMeasureState.Ready) ?: return
        newState.copy(showHelp = res).sendToState()
    }

    override fun toggleShowMethod() {
        if (state.value.currentReadOnly) return
        val newState = (state.value as? EditMeasureState.Ready) ?: return

        newState.copy(showMethod = !newState.showMethod).sendToState()
    }

    override fun init(initialMeasure: MeasureData?) {
        EditMeasureState.Loading(
            initialMeasure, initialMeasure != null
        ).sendToState()

        viewModelScope.launch(Dispatchers.IO) {
            val userSettingsLoaded = userSettingsRepository.findCurrent()
            userSettings = userSettingsLoaded
            val lastMeasure = measuresRepository.findLast()

            val measureLoaded = initialMeasure?.let { measuresRepository.findById(it.uid) }
                ?: lastMeasure?.let { Measure.newMeasure(it) }
                ?: Measure.newMeasure(userSettingsLoaded)
            measure = measureLoaded

            EditMeasureState.Ready(
                userSettings = userSettingsLoaded.value,
                measure = measureLoaded.value,
                showHelp = null,
                showMethod = false,
                readOnly = initialMeasure != null
            ).sendToState()
        }
    }

    companion object
}
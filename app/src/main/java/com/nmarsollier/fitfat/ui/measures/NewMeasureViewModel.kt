package com.nmarsollier.fitfat.ui.measures

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.nmarsollier.fitfat.model.*
import com.nmarsollier.fitfat.ui.utils.BaseViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.*

sealed class NewMeasureState {
    object Initial : NewMeasureState()
    object Loading : NewMeasureState()
    object Invalid : NewMeasureState()
    object Close : NewMeasureState()

    data class Ready(
        val userSettings: UserSettings,
        val lastMeasure: Measure?,
        val measure: Measure
    ) : NewMeasureState()
}

class NewMeasureViewModel : BaseViewModel<NewMeasureState>(NewMeasureState.Initial) {
    private var userSettings: UserSettings? = null
    private var lastMeasure: Measure? = null
    private var currentMeasure: Measure = Measure.newMeasure()

    fun load(
        context: Context
    ) = viewModelScope.launch {
        UserSettingsRepository.load(context).firstOrNull {
            userSettings = it
            updateState()
            true
        }

        MeasuresRepository.findLast(context).firstOrNull {
            lastMeasure = it
            updateState()
            true
        }
    }

    private fun updateState() {
        userSettings?.let { userSettings ->
            state.emit(
                when (val value = state.value) {
                    NewMeasureState.Initial -> NewMeasureState.Ready(
                        userSettings = userSettings,
                        lastMeasure = lastMeasure,
                        measure = currentMeasure
                    )
                    NewMeasureState.Loading -> NewMeasureState.Ready(
                        userSettings = userSettings,
                        lastMeasure = lastMeasure,
                        measure = currentMeasure
                    )
                    is NewMeasureState.Ready -> value.copy(userSettings = userSettings)
                    else -> NewMeasureState.Initial
                }
            )
        }
    }

    fun saveMeasure(context: Context) = viewModelScope.launch {
        val currentState = state.value
        val measure = (currentState as? NewMeasureState.Ready)?.measure ?: return@launch
        val userSettings = (currentState as? NewMeasureState.Ready)?.userSettings ?: return@launch

        state.emit(NewMeasureState.Loading)

        if (measure.isValid()) {
            getRoomDatabase(context).measureDao().insert(measure)

            userSettings.weight = measure.bodyWeight
            getRoomDatabase(context).userDao().update(userSettings)

            //FirebaseDao.uploadPendingMeasures(context)

            state.emit(NewMeasureState.Close)
        } else {
            state.emit(NewMeasureState.Invalid)
            state.emit(currentState)
        }
    }

    fun updateDate(time: Date) {
        (state.value as? NewMeasureState.Ready)?.let {
            state.emit(
                it.copy(
                    measure = it.measure.copy(
                        date = time
                    )
                )
            )
        }
    }

    fun updateMeasureValue(measureValue: MeasureValue, value: Number) {
        currentMeasure.setValueForMethod(measureValue, value)
        (state.value as? NewMeasureState.Ready)?.let {
            state.emit(
                it.copy(
                    measure = currentMeasure
                )
            )
        }
    }
}
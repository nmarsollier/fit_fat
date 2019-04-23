package com.nmarsollier.fitfat.ui.measures

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.nmarsollier.fitfat.model.db.getRoomDatabase
import com.nmarsollier.fitfat.model.measures.Measure
import com.nmarsollier.fitfat.model.measures.MeasureValue
import com.nmarsollier.fitfat.model.measures.MeasuresRepository
import com.nmarsollier.fitfat.model.userSettings.UserSettings
import com.nmarsollier.fitfat.model.userSettings.UserSettingsRepository
import com.nmarsollier.fitfat.ui.utils.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
    private val currentMeasure: Measure?
        get() = (state.value as? NewMeasureState.Ready)?.measure

    fun load(
        context: Context
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            UserSettingsRepository.load(context).collect {
                userSettings = it
                updateState()
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            MeasuresRepository.findLast(context).collect {
                lastMeasure = it
                updateState()
            }
        }
    }

    private fun updateState() {
        userSettings?.let { userSettings ->
            mutableState.update {
                when (val value = state.value) {
                    NewMeasureState.Initial -> NewMeasureState.Ready(
                        userSettings = userSettings,
                        lastMeasure = lastMeasure,
                        measure = currentMeasure ?: Measure.newMeasure()
                    )
                    NewMeasureState.Loading -> NewMeasureState.Ready(
                        userSettings = userSettings,
                        lastMeasure = lastMeasure,
                        measure = currentMeasure ?: Measure.newMeasure()
                    )
                    is NewMeasureState.Ready -> value.copy(userSettings = userSettings)
                    else -> NewMeasureState.Initial
                }
            }
        }
    }

    fun saveMeasure(context: Context) = viewModelScope.launch {
        val currentState = state.value
        val measure = currentMeasure ?: return@launch
        val userSettings = (currentState as? NewMeasureState.Ready)?.userSettings ?: return@launch

        mutableState.update {
            NewMeasureState.Loading
        }

        if (measure.isValid()) {
            withContext(Dispatchers.IO) {
                MeasuresRepository.insert(context, measure)
                if (measure.bodyWeight > 0) {
                    userSettings.weight = measure.bodyWeight
                    getRoomDatabase(context).userDao().update(userSettings)
                }
            }

            mutableState.update {
                NewMeasureState.Close
            }
        } else {
            mutableState.update {
                NewMeasureState.Invalid
            }
            mutableState.update {
                currentState
            }
        }
    }

    fun updateDate(time: Date) {
        (state.value as? NewMeasureState.Ready)?.let { newState ->
            mutableState.update {
                newState.copy(
                    measure = newState.measure.copy(
                        date = time
                    )
                )
            }
        }
    }

    fun updateMeasureValue(measureValue: MeasureValue, value: Number) {
        mutableState.getAndUpdate {
            (it as? NewMeasureState.Ready)?.let { newState ->
                it.copy(
                    measure = it.measure.copy().apply {
                        setValueForMethod(measureValue, value)
                    }
                )
            } ?: it
        }
    }
}
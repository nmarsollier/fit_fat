package com.nmarsollier.fitfat.utils

import androidx.activity.ComponentActivity
import com.nmarsollier.fitfat.model.measures.Measure
import com.nmarsollier.fitfat.model.measures.MeasureMethod
import com.nmarsollier.fitfat.model.measures.MeasureValue
import com.nmarsollier.fitfat.model.userSettings.UserSettingsEntity
import com.nmarsollier.fitfat.model.userSettings.UserSettingsEntity.MeasureType
import com.nmarsollier.fitfat.model.userSettings.UserSettingsEntity.SexType
import com.nmarsollier.fitfat.ui.dashboard.DashboardReducer
import com.nmarsollier.fitfat.ui.dashboard.DashboardViewModel
import com.nmarsollier.fitfat.ui.dashboard.Screen
import com.nmarsollier.fitfat.ui.editMeasure.EditMeasureReducer
import com.nmarsollier.fitfat.ui.editMeasure.EditMeasureViewModel
import com.nmarsollier.fitfat.ui.measuresList.MeasuresListReducer
import com.nmarsollier.fitfat.ui.measuresList.MeasuresListViewModel
import com.nmarsollier.fitfat.ui.options.OptionsReducer
import com.nmarsollier.fitfat.ui.options.OptionsViewModel
import com.nmarsollier.fitfat.ui.stats.StatsReducer
import com.nmarsollier.fitfat.ui.stats.StatsViewModel
import java.util.Date

interface MeasureSamples {
    val simpleData: List<Measure>
    val bodyFat: Measure
}

val Measure.Companion.Samples
    get() = object : MeasureSamples {
        override val simpleData = listOf(
            Measure(
                uid = "123",
                bodyWeight = 50.0,
                bodyHeight = 70.0,
                age = 45,
                sex = SexType.FEMALE,
                date = dateOf(2018, 12, 12),
                measureMethod = MeasureMethod.WEIGHT_ONLY,
                chest = 5,
                abdominal = 6,
                thigh = 7,
                tricep = 8,
                subscapular = 3,
                suprailiac = 4,
                midaxillary = 5,
                bicep = 6,
                lowerBack = 7,
                calf = 8,
                fatPercent = 12.5,
                cloudSync = false
            ), Measure(
                uid = "123",
                bodyWeight = 80.0,
                bodyHeight = 70.0,
                age = 45,
                sex = SexType.FEMALE,
                date = dateOf(2018, 12, 13),
                measureMethod = MeasureMethod.WEIGHT_ONLY,
                chest = 5,
                abdominal = 6,
                thigh = 7,
                tricep = 8,
                subscapular = 3,
                suprailiac = 4,
                midaxillary = 5,
                bicep = 6,
                lowerBack = 7,
                calf = 8,
                fatPercent = 12.5,
                cloudSync = false
            )
        )

        override val bodyFat = Measure(
            uid = "123",
            bodyWeight = 90.0,
            bodyHeight = 90.0,
            age = 45,
            sex = SexType.FEMALE,
            date = dateOf(2018, 12, 14),
            measureMethod = MeasureMethod.WEIGHT_ONLY,
            chest = 5,
            abdominal = 6,
            thigh = 7,
            tricep = 8,
            subscapular = 3,
            suprailiac = 4,
            midaxillary = 5,
            bicep = 6,
            lowerBack = 7,
            calf = 8,
            fatPercent = 12.5,
            cloudSync = false
        )
    }

interface UserSettingsSamples {
    val simpleData: UserSettingsEntity
}

val UserSettingsEntity.Companion.Samples
    get() = object : UserSettingsSamples {
        override val simpleData = UserSettingsEntity(
            uid = 0,
            displayName = "Nestor Marsollier",
            birthDate = dateOf(2000, 12, 14),
            weight = 80.0,
            height = 183.0,
            sex = SexType.FEMALE,
            measureSystem = MeasureType.METRIC,
            firebaseToken = null
        )
    }

interface DashboardViewModelSamples {
    fun reducer(): DashboardReducer
}

val DashboardViewModel.Companion.Samples: DashboardViewModelSamples
    get() = object : DashboardViewModelSamples {
        override fun reducer() = object : DashboardReducer {
            override fun setCurrentSelectedTab(screen: Screen) = Unit
        }
    }


interface MeasuresListViewModelSamples {
    fun reducer(): MeasuresListReducer
}

val MeasuresListViewModel.Companion.Samples: MeasuresListViewModelSamples
    get() = object : MeasuresListViewModelSamples {
        override fun reducer() = object : MeasuresListReducer {
            override fun load() = Unit
            override fun deleteMeasure(measure: Measure) = Unit
            override fun openNewMeasure() = Unit
            override fun openViewMeasure(measure: Measure) = Unit
        }
    }


interface EditMeasureViewModelSamples {
    fun reducer(): EditMeasureReducer
}

val EditMeasureViewModel.Companion.Samples: EditMeasureViewModelSamples
    get() = object : EditMeasureViewModelSamples {
        override fun reducer() = object : EditMeasureReducer {
            override fun saveMeasure() = Unit

            override fun updateDate(time: Date) = Unit

            override fun updateMeasureMethod(measureMethod: MeasureMethod) = Unit

            override fun updateMeasureValue(measureValue: MeasureValue, value: Number) = Unit

            override fun close() = Unit

            override fun toggleHelp(res: Int?) = Unit

            override fun toggleShowMethod() = Unit

            override fun init(initialMeasure: Measure?) = Unit
        }
    }

interface OptionsViewModelSamples {
    fun reducer(): OptionsReducer
}

val OptionsViewModel.Companion.Samples: OptionsViewModelSamples
    get() = object : OptionsViewModelSamples {
        override fun reducer() = object : OptionsReducer {
            override fun loginWithGoogle(activity: ComponentActivity) = Unit

            override fun disableFirebase() = Unit

            override fun updateSex(newSex: SexType) = Unit

            override fun updateMeasureSystem(system: MeasureType) = Unit

            override fun updateWeight(newWeight: Double) = Unit

            override fun updateHeight(newHeight: Double) = Unit

            override fun updateDisplayName(newName: String) = Unit

            override fun updateBirthDate(newBirthDate: Date) = Unit

            override fun saveSettings() = Unit

            override fun load() = Unit

        }
    }

interface StatsViewModelSamples {
    fun reducer(): StatsReducer
}

val StatsViewModel.Companion.Samples: StatsViewModelSamples
    get() = object : StatsViewModelSamples {
        override fun reducer() = object : StatsReducer {
            override fun init() = Unit
            override fun updateMethod(selectedMethod: MeasureMethod) = Unit
            override fun toggleShowMethod() = Unit
        }
    }

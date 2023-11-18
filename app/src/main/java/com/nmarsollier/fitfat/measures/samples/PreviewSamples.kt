package com.nmarsollier.fitfat.measures.samples

import com.nmarsollier.fitfat.measures.model.Measure
import com.nmarsollier.fitfat.measures.model.asMeasure
import com.nmarsollier.fitfat.measures.model.db.MeasureData
import com.nmarsollier.fitfat.measures.model.db.MeasureMethod
import com.nmarsollier.fitfat.measures.model.db.MeasureValue
import com.nmarsollier.fitfat.measures.ui.edit.EditMeasureReducer
import com.nmarsollier.fitfat.measures.ui.edit.EditMeasureViewModel
import com.nmarsollier.fitfat.measures.ui.list.MeasuresListReducer
import com.nmarsollier.fitfat.measures.ui.list.MeasuresListViewModel
import com.nmarsollier.fitfat.userSettings.model.db.UserSettingsData
import com.nmarsollier.fitfat.common.converters.dateOf
import java.util.Date

interface MeasureSamples {
    val simpleData: List<Measure>
    val bodyFat: Measure
}

val Measure.Companion.Samples
    get() = object : MeasureSamples {
        override val simpleData = listOf(
            MeasureData(
                uid = "123",
                bodyWeight = 50.0,
                bodyHeight = 70.0,
                age = 45,
                sex = UserSettingsData.SexType.FEMALE,
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
            ),
            MeasureData(
                uid = "123",
                bodyWeight = 80.0,
                bodyHeight = 70.0,
                age = 45,
                sex = UserSettingsData.SexType.FEMALE,
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
        ).map { it.asMeasure }

        override val bodyFat = MeasureData(
            uid = "123",
            bodyWeight = 90.0,
            bodyHeight = 90.0,
            age = 45,
            sex = UserSettingsData.SexType.FEMALE,
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
        ).asMeasure
    }


interface MeasuresListViewModelSamples {
    fun reducer(): MeasuresListReducer
}

val MeasuresListViewModel.Companion.Samples: MeasuresListViewModelSamples
    get() = object : MeasuresListViewModelSamples {
        override fun reducer() = object :
            MeasuresListReducer {
            override fun load() = Unit
            override fun deleteMeasure(measure: MeasureData) =
                Unit

            override fun openNewMeasure() = Unit
            override fun openViewMeasure(measure: MeasureData) =
                Unit
        }
    }


interface EditMeasureViewModelSamples {
    fun reducer(): EditMeasureReducer
}

val EditMeasureViewModel.Companion.Samples: EditMeasureViewModelSamples
    get() = object : EditMeasureViewModelSamples {
        override fun reducer() = object :
            EditMeasureReducer {
            override fun saveMeasure() = Unit

            override fun updateDate(time: Date) = Unit

            override fun updateMeasureMethod(measureMethod: MeasureMethod) =
                Unit

            override fun updateMeasureValue(
                measureValue: MeasureValue,
                value: Number
            ) = Unit

            override fun close() = Unit

            override fun toggleHelp(res: Int?) = Unit

            override fun toggleShowMethod() = Unit

            override fun init(initialMeasure: MeasureData?) = Unit
        }
    }

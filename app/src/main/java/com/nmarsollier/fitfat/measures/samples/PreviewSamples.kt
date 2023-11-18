package com.nmarsollier.fitfat.measures.samples

import com.nmarsollier.fitfat.common.converters.dateOf
import com.nmarsollier.fitfat.common.ui.viewModel.Reducer
import com.nmarsollier.fitfat.measures.model.Measure
import com.nmarsollier.fitfat.measures.model.asMeasure
import com.nmarsollier.fitfat.measures.model.db.MeasureData
import com.nmarsollier.fitfat.measures.model.db.MeasureMethod
import com.nmarsollier.fitfat.measures.ui.edit.EditMeasureEvent
import com.nmarsollier.fitfat.measures.ui.edit.EditMeasureView
import com.nmarsollier.fitfat.measures.ui.list.MeasuresListEvent
import com.nmarsollier.fitfat.measures.ui.list.MeasuresListView
import com.nmarsollier.fitfat.userSettings.model.db.UserSettingsData

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
    fun reducer(): Reducer<MeasuresListEvent>
}

val MeasuresListView.Companion.Samples: MeasuresListViewModelSamples
    get() = object : MeasuresListViewModelSamples {
        override fun reducer() = object : Reducer<MeasuresListEvent> {
            override fun reduce(event: MeasuresListEvent) = Unit
        }
    }


interface EditMeasureViewModelSamples {
    fun reducer(): Reducer<EditMeasureEvent>
}

val EditMeasureView.Companion.Samples: EditMeasureViewModelSamples
    get() = object : EditMeasureViewModelSamples {
        override fun reducer() = object : Reducer<EditMeasureEvent> {
            override fun reduce(event: EditMeasureEvent) = Unit
        }
    }

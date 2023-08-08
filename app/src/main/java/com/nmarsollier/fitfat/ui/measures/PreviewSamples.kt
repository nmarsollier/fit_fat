package com.nmarsollier.fitfat.ui.measures

import com.nmarsollier.fitfat.utils.dateOf
import com.nmarsollier.fitfat.models.measures.Measure
import com.nmarsollier.fitfat.models.measures.db.MeasureMethod
import com.nmarsollier.fitfat.models.userSettings.db.SexType

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
            ),
            Measure(
                uid = "124",
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
            uid = "121",
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

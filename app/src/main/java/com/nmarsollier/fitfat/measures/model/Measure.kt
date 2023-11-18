package com.nmarsollier.fitfat.measures.model

import com.nmarsollier.fitfat.measures.model.db.MeasureData
import com.nmarsollier.fitfat.measures.model.db.MeasureMethod
import com.nmarsollier.fitfat.measures.model.db.MeasureValue
import com.nmarsollier.fitfat.userSettings.model.UserSettings
import com.nmarsollier.fitfat.userSettings.model.db.UserSettingsData
import com.nmarsollier.fitfat.utils.converters.toPounds
import java.util.Date
import java.util.UUID
import kotlin.math.log10

data class Measure internal constructor(
    private var rootEntity: MeasureData
) {
    val value
        get() = rootEntity

    fun isValid(): Boolean = valuesRequiredForMethod(rootEntity.measureMethod)
        .all { it.isValid(rootEntity) }

    fun recalculateFatPercent() {
        if (!isValid()) {
            rootEntity = rootEntity.copy(
                fatPercent = 0.0
            )
        }

        rootEntity = rootEntity.copy(
            fatPercent = rootEntity.measureMethod.fatPercent(rootEntity)
        )
    }

    fun updateDate(time: Date) {
        rootEntity = rootEntity.copy(date = time)
    }

    fun updateMethodValue(measureValue: MeasureValue, newValue: Number) {
        rootEntity = when (measureValue) {
            MeasureValue.CHEST -> rootEntity.copy(chest = newValue.toInt())
            MeasureValue.ABDOMINAL -> rootEntity.copy(abdominal = newValue.toInt())
            MeasureValue.THIGH -> rootEntity.copy(thigh = newValue.toInt())
            MeasureValue.TRICEP -> rootEntity.copy(tricep = newValue.toInt())
            MeasureValue.SUBSCAPULAR -> rootEntity.copy(subscapular = newValue.toInt())
            MeasureValue.SUPRAILIAC -> rootEntity.copy(suprailiac = newValue.toInt())
            MeasureValue.MIDAXILARITY -> rootEntity.copy(midaxillary = newValue.toInt())
            MeasureValue.BICEP -> rootEntity.copy(bicep = newValue.toInt())
            MeasureValue.LOWER_BACK -> rootEntity.copy(lowerBack = newValue.toInt())
            MeasureValue.CALF -> rootEntity.copy(calf = newValue.toInt())
            MeasureValue.BODY_WEIGHT -> rootEntity.copy(bodyWeight = newValue.toDouble())
            MeasureValue.BODY_FAT -> rootEntity.copy(fatPercent = newValue.toDouble())
        }
        recalculateFatPercent()
    }

    fun updateMeasureMethod(measureMethod: MeasureMethod) {
        rootEntity = rootEntity.copy(
            measureMethod = measureMethod
        )
    }

    fun updateCloudSync(sync: Boolean) {
        rootEntity = rootEntity.copy(cloudSync = sync)
    }

    private fun valuesRequiredForMethod(method: MeasureMethod) =
        MeasureValue.values().filter { it.isRequiredForMethod(method) }

    companion object {
        fun newMeasure(userSettings: UserSettings) =
            MeasureData(
                sex = userSettings.value.sex,
                bodyHeight = userSettings.value.height,
                bodyWeight = userSettings.value.weight,
                measureMethod = MeasureMethod.DURNIN_WOMERSLEY,
            ).asMeasure

        fun newMeasure(measure: Measure) =
            measure.rootEntity.copy(
                uid = UUID.randomUUID().toString(),
                date = Date()
            ).asMeasure
    }
}

internal val MeasureData.asMeasure: Measure
    get() = Measure(this)

val MeasureData.bodyFatMass: Double
    get() = if (bodyWeight > 0 && fatPercent > 0) {
        bodyWeight * (fatPercent / 100)
    } else {
        0.0
    }

val MeasureData.leanWeight: Double
    get() = if (bodyWeight > 0 && fatPercent > 0) {
        bodyWeight * (1 - (fatPercent / 100))
    } else {
        0.0
    }

val MeasureData.freeFatMassIndex: Double
    get() {
        val lw = leanWeight
        val bh = bodyHeight
        return if (lw > 0 && bh > 0) {
            lw / ((bh / 100) * (bh / 100))
        } else {
            0.0
        }
    }

fun MeasureMethod.fatPercent(measureData: MeasureData): Double {
    fun jacksonPollock7(measureData: MeasureData): Double {
        val sum =
            measureData.chest + measureData.abdominal + measureData.thigh +
                    measureData.tricep + measureData.subscapular +
                    measureData.suprailiac + measureData.midaxillary
        val density = if (measureData.sex == UserSettingsData.SexType.MALE) {
            1.112 - (0.00043499 * sum) + (0.00000055 * sum * sum) - (0.00028826 * measureData.age)
        } else {
            1.097 - (0.00046971 * sum) + (0.00000056 * sum * sum) - (0.00012828 * measureData.age)
        }
        return 495 / density - 450
    }

    fun jacksonPollock3(measureData: MeasureData): Double {
        val sum = measureData.abdominal + measureData.thigh + measureData.chest
        val density =
            if (measureData.sex == UserSettingsData.SexType.MALE) {
                1.10938 - (0.0008267 * sum) + (0.0000016 * sum * sum) - (0.0002574 * measureData.age)
            } else {
                1.0994291 - (0.0009929 * sum) + (0.0000023 * sum * sum) - (0.0001392 * measureData.age)
            }
        return 495 / density - 450
    }

    fun jacksonPollock4(measureData: MeasureData): Double {
        val sum =
            measureData.abdominal + measureData.thigh + measureData.tricep + measureData.suprailiac

        return if (measureData.sex == UserSettingsData.SexType.MALE) {
            (0.29288 * sum) - (0.0005 * sum * sum) + (0.15845 * measureData.age) - 5.76377
        } else {
            (0.29669 * sum) - (0.00043 * sum * sum) + (0.02963 * measureData.age) - 1.4072
        }
    }

    fun parrillo(measureData: MeasureData): Double {
        val sum = measureData.chest + measureData.abdominal + measureData.thigh +
                measureData.bicep + measureData.tricep + measureData.subscapular +
                measureData.suprailiac + measureData.lowerBack + measureData.calf
        return 27 * sum / measureData.bodyWeight.toPounds()
    }

    fun durninWomersley(measureData: MeasureData): Double {
        val logSum =
            log10((measureData.bicep + measureData.tricep + measureData.subscapular + measureData.suprailiac).toDouble())

        val density = when (measureData.sex) {
            UserSettingsData.SexType.MALE -> {
                when (measureData.age) {
                    in 0..16 -> 1.1533 - (0.0643 * logSum)
                    in 17..19 -> 1.1620 - (0.0630 * logSum)
                    in 20..29 -> 1.1631 - (0.0632 * logSum)
                    in 30..39 -> 1.1422 - (0.0544 * logSum)
                    in 40..49 -> 1.1620 - (0.0700 * logSum)
                    else -> 1.1715 - (0.0779 * logSum)
                }
            }

            UserSettingsData.SexType.FEMALE -> {
                when (measureData.age) {
                    in 0..17 -> 1.1369 - (0.0598 * logSum)
                    in 18..19 -> 1.1549 - (0.0678 * logSum)
                    in 20..29 -> 1.1599 - (0.0717 * logSum)
                    in 30..39 -> 1.1423 - (0.0632 * logSum)
                    in 40..49 -> 1.1333 - (0.0612 * logSum)
                    else -> 1.1339 - (0.0645 * logSum)
                }
            }
        }
        return 495 / density - 450
    }

    fun fromScale(measureData: MeasureData): Double = measureData.fatPercent

    fun weightOnly(): Double = 0.0

    return when (this) {
        MeasureMethod.JACKSON_POLLOCK_7 -> jacksonPollock7(measureData)
        MeasureMethod.JACKSON_POLLOCK_3 -> jacksonPollock3(measureData)
        MeasureMethod.JACKSON_POLLOCK_4 -> jacksonPollock4(measureData)
        MeasureMethod.PARRILLO -> parrillo(measureData)
        MeasureMethod.DURNIN_WOMERSLEY -> durninWomersley(measureData)
        MeasureMethod.FROM_SCALE -> fromScale(measureData)
        MeasureMethod.WEIGHT_ONLY -> weightOnly()
    }
}


fun MeasureValue.isRequiredForMethod(method: MeasureMethod): Boolean {
    return this.requiredFor.contains(method)
}

fun MeasureValue.isValid(measureData: MeasureData): Boolean = when (this) {
    MeasureValue.BODY_WEIGHT -> measureData.bodyWeight
    MeasureValue.CHEST -> measureData.chest.toDouble()
    MeasureValue.ABDOMINAL -> measureData.abdominal.toDouble()
    MeasureValue.THIGH -> measureData.thigh.toDouble()
    MeasureValue.TRICEP -> measureData.tricep.toDouble()
    MeasureValue.SUBSCAPULAR -> measureData.subscapular.toDouble()
    MeasureValue.SUPRAILIAC -> measureData.suprailiac.toDouble()
    MeasureValue.MIDAXILARITY -> measureData.midaxillary.toDouble()
    MeasureValue.BICEP -> measureData.bicep.toDouble()
    MeasureValue.LOWER_BACK -> measureData.lowerBack.toDouble()
    MeasureValue.CALF -> measureData.calf.toDouble()
    MeasureValue.BODY_FAT -> measureData.fatPercent
} > 0

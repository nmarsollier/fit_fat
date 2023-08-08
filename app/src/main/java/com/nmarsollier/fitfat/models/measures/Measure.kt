package com.nmarsollier.fitfat.models.measures

import androidx.compose.runtime.*
import com.nmarsollier.fitfat.models.measures.db.*
import com.nmarsollier.fitfat.models.userSettings.*
import com.nmarsollier.fitfat.models.userSettings.db.*
import java.util.*

@Stable
@Immutable
data class Measure(
    val uid: String,
    val bodyWeight: Double,
    val bodyHeight: Double,
    val age: Int = 0,
    val sex: SexType,
    val date: Date = Date(),
    val measureMethod: MeasureMethod,
    val chest: Int = 0,
    val abdominal: Int = 0,
    val thigh: Int = 0,
    val tricep: Int = 0,
    val subscapular: Int = 0,
    val suprailiac: Int = 0,
    val midaxillary: Int = 0,
    val bicep: Int = 0,
    val lowerBack: Int = 0,
    val calf: Int = 0,
    val fatPercent: Double = 0.0,
    val cloudSync: Boolean = false
) {
    val bodyFatMass: Double
        get() = if (bodyWeight > 0 && fatPercent > 0) {
            bodyWeight * (fatPercent / 100)
        } else {
            0.0
        }

    val leanWeight: Double
        get() = if (bodyWeight > 0 && fatPercent > 0) {
            bodyWeight * (1 - (fatPercent / 100))
        } else {
            0.0
        }

    val freeFatMassIndex: Double
        get() {
            val lw = leanWeight
            val bh = bodyHeight
            return if (lw > 0 && bh > 0) {
                lw / ((bh / 100) * (bh / 100))
            } else {
                0.0
            }
        }

    companion object {
        fun newMeasure(userSettings: UserSettings) =
            MeasureEntity(
                sex = userSettings.sex,
                bodyHeight = userSettings.height,
                bodyWeight = userSettings.weight,
                measureMethod = MeasureMethod.DURNIN_WOMERSLEY,
            ).asMeasure

        fun newMeasure(measure: Measure) =
            measure.copy(
                uid = UUID.randomUUID().toString(),
                date = Date()
            )
    }
}

internal val MeasureEntity.asMeasure: Measure
    get() = Measure(
        uid = this.uid,
        bodyWeight = this.bodyWeight,
        bodyHeight = this.bodyHeight,
        age = this.age,
        sex = this.sex,
        date = this.date,
        measureMethod = this.measureMethod,
        chest = this.chest,
        abdominal = this.abdominal,
        thigh = this.thigh,
        tricep = this.tricep,
        subscapular = this.subscapular,
        suprailiac = this.suprailiac,
        midaxillary = this.midaxillary,
        bicep = this.bicep,
        lowerBack = this.lowerBack,
        calf = this.calf,
        fatPercent = this.fatPercent,
        cloudSync = this.cloudSync
    )


fun Measure.recalculateFatPercent(): Measure {
    if (!isValid) {
        return this.copy(
            fatPercent = 0.0
        )
    }

    return this.copy(
        fatPercent = measureMethod.fatPercent(this)
    )
}

fun Measure.updateDate(time: Date): Measure {
    return copy(date = time)
}

fun Measure.updateMethodValue(measureValue: MeasureValue, newValue: Number): Measure {
    return when (measureValue) {
        MeasureValue.CHEST -> copy(chest = newValue.toInt())
        MeasureValue.ABDOMINAL -> copy(abdominal = newValue.toInt())
        MeasureValue.THIGH -> copy(thigh = newValue.toInt())
        MeasureValue.TRICEP -> copy(tricep = newValue.toInt())
        MeasureValue.SUBSCAPULAR -> copy(subscapular = newValue.toInt())
        MeasureValue.SUPRAILIAC -> copy(suprailiac = newValue.toInt())
        MeasureValue.MIDAXILARITY -> copy(midaxillary = newValue.toInt())
        MeasureValue.BICEP -> copy(bicep = newValue.toInt())
        MeasureValue.LOWER_BACK -> copy(lowerBack = newValue.toInt())
        MeasureValue.CALF -> copy(calf = newValue.toInt())
        MeasureValue.BODY_WEIGHT -> copy(bodyWeight = newValue.toDouble())
        MeasureValue.BODY_FAT -> copy(fatPercent = newValue.toDouble())
    }.recalculateFatPercent()
}

fun Measure.updateMeasureMethod(measureMethod: MeasureMethod): Measure {
    return copy(
        measureMethod = measureMethod
    )
}

fun Measure.updateCloudSync(sync: Boolean): Measure {
    return copy(cloudSync = sync)
}

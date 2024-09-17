package com.nmarsollier.fitfat.models.measures

import com.nmarsollier.fitfat.models.measures.db.MeasureMethod
import com.nmarsollier.fitfat.models.measures.db.MeasureValue

val Measure.isValid
    get(): Boolean = this.measureMethod.valuesRequired().all { it.isValid(this) }

private fun MeasureMethod.valuesRequired() =
    MeasureValue.entries.filter { it.isRequiredForMethod(this) }

fun MeasureValue.isRequiredForMethod(method: MeasureMethod): Boolean {
    return this.requiredFor.contains(method)
}

fun MeasureValue.isValid(measure: Measure): Boolean = when (this) {
    MeasureValue.BODY_WEIGHT -> measure.bodyWeight
    MeasureValue.CHEST -> measure.chest.toDouble()
    MeasureValue.ABDOMINAL -> measure.abdominal.toDouble()
    MeasureValue.THIGH -> measure.thigh.toDouble()
    MeasureValue.TRICEP -> measure.tricep.toDouble()
    MeasureValue.SUBSCAPULAR -> measure.subscapular.toDouble()
    MeasureValue.SUPRAILIAC -> measure.suprailiac.toDouble()
    MeasureValue.MIDAXILARITY -> measure.midaxillary.toDouble()
    MeasureValue.BICEP -> measure.bicep.toDouble()
    MeasureValue.LOWER_BACK -> measure.lowerBack.toDouble()
    MeasureValue.CALF -> measure.calf.toDouble()
    MeasureValue.BODY_FAT -> measure.fatPercent
} > 0

package com.nmarsollier.fitfat.ui.measures

import com.nmarsollier.fitfat.R
import com.nmarsollier.fitfat.models.measures.Measure
import com.nmarsollier.fitfat.models.measures.db.MeasureMethod
import com.nmarsollier.fitfat.models.measures.db.MeasureValue
import com.nmarsollier.fitfat.models.userSettings.UserSettings
import com.nmarsollier.fitfat.ui.userSettings.displayWeight

val MeasureMethod.labelRes: Int
    get() = when (this) {
        MeasureMethod.JACKSON_POLLOCK_7 -> R.string.measure_method_jackson_pollock_7
        MeasureMethod.JACKSON_POLLOCK_3 -> R.string.measure_method_jackson_pollock_3
        MeasureMethod.JACKSON_POLLOCK_4 -> R.string.measure_method_jackson_pollock_4
        MeasureMethod.PARRILLO -> R.string.measure_method_parrillo
        MeasureMethod.DURNIN_WOMERSLEY -> R.string.measure_method_durnin_womersley
        MeasureMethod.FROM_SCALE -> R.string.measure_method_manual_scale
        MeasureMethod.WEIGHT_ONLY -> R.string.measure_method_weight
    }

val MeasureValue.colorRes: Int
    get() = when (this) {
        MeasureValue.BODY_WEIGHT -> R.color.chartBodyWeight
        MeasureValue.CHEST -> R.color.chartChest
        MeasureValue.ABDOMINAL -> R.color.chartAbdominal
        MeasureValue.THIGH -> R.color.chartThigh
        MeasureValue.TRICEP -> R.color.chartTricep
        MeasureValue.SUBSCAPULAR -> R.color.chartSubscapular
        MeasureValue.SUPRAILIAC -> R.color.chartSuprailiac
        MeasureValue.MIDAXILARITY -> R.color.chartMidaxilarity
        MeasureValue.BICEP -> R.color.chartBicep
        MeasureValue.LOWER_BACK -> R.color.chartLowerBack
        MeasureValue.CALF -> R.color.chartCalf
        MeasureValue.BODY_FAT -> R.color.chartBodyFat
    }

val MeasureValue.helpRes: Int?
    get() = when (this) {
        MeasureValue.BODY_WEIGHT -> null
        MeasureValue.CHEST -> R.drawable.img_chest
        MeasureValue.ABDOMINAL -> R.drawable.img_abdominal
        MeasureValue.THIGH -> R.drawable.img_thigh
        MeasureValue.TRICEP -> R.drawable.img_tricep
        MeasureValue.SUBSCAPULAR -> R.drawable.img_subscapular
        MeasureValue.SUPRAILIAC -> R.drawable.img_suprailiac
        MeasureValue.MIDAXILARITY -> R.drawable.img_midaxilarity
        MeasureValue.BICEP -> R.drawable.img_bicep
        MeasureValue.LOWER_BACK -> R.drawable.img_lower_back
        MeasureValue.CALF -> R.drawable.img_calf
        MeasureValue.BODY_FAT -> null
    }

fun Measure.displayValue(
    measureValue: MeasureValue,
    userSettings: UserSettings
) = measureValue.displayValue(this, userSettings)

fun MeasureValue.displayValue(
    measureEntity: Measure,
    userSettings: UserSettings
): Double =
    when (this) {
        MeasureValue.CHEST -> measureEntity.chest.toDouble()
        MeasureValue.ABDOMINAL -> measureEntity.abdominal.toDouble()
        MeasureValue.THIGH -> measureEntity.thigh.toDouble()
        MeasureValue.TRICEP -> measureEntity.tricep.toDouble()
        MeasureValue.SUBSCAPULAR -> measureEntity.subscapular.toDouble()
        MeasureValue.SUPRAILIAC -> measureEntity.suprailiac.toDouble()
        MeasureValue.MIDAXILARITY -> measureEntity.midaxillary.toDouble()
        MeasureValue.BICEP -> measureEntity.bicep.toDouble()
        MeasureValue.LOWER_BACK -> measureEntity.lowerBack.toDouble()
        MeasureValue.CALF -> measureEntity.calf.toDouble()
        MeasureValue.BODY_WEIGHT -> userSettings.displayWeight(measureEntity.bodyWeight)
        MeasureValue.BODY_FAT -> measureEntity.fatPercent
    }


fun Measure.calculateIntPart(
    progress: Int,
    measureValue: MeasureValue,
    userSettings: UserSettings
): Double {
    val currentValue = displayValue(measureValue, userSettings)
    val decimal = currentValue - currentValue.toInt()
    return progress.toDouble() + decimal
}


fun Measure.calculateDecimalPart(
    progress: Int,
    measureValue: MeasureValue,
    userSettings: UserSettings
): Double {
    val currentValue = displayValue(measureValue, userSettings).toInt()

    return currentValue.toDouble() + (progress.toDouble() / 10.0)
}
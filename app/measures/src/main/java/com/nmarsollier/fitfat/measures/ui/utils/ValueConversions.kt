package com.nmarsollier.fitfat.measures.ui.utils

import com.nmarsollier.fitfat.measures.R
import com.nmarsollier.fitfat.measures.model.db.MeasureData
import com.nmarsollier.fitfat.measures.model.db.MeasureMethod
import com.nmarsollier.fitfat.measures.model.db.MeasureValue
import com.nmarsollier.fitfat.userSettings.model.db.UserSettingsData
import com.nmarsollier.fitfat.userSettings.ui.utils.displayWeight
import com.nmarsollier.fitfat.utils.R as UR

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
        MeasureValue.BODY_WEIGHT -> UR.color.chartBodyWeight
        MeasureValue.CHEST -> UR.color.chartChest
        MeasureValue.ABDOMINAL -> UR.color.chartAbdominal
        MeasureValue.THIGH -> UR.color.chartThigh
        MeasureValue.TRICEP -> UR.color.chartTricep
        MeasureValue.SUBSCAPULAR -> UR.color.chartSubscapular
        MeasureValue.SUPRAILIAC -> UR.color.chartSuprailiac
        MeasureValue.MIDAXILARITY -> UR.color.chartMidaxilarity
        MeasureValue.BICEP -> UR.color.chartBicep
        MeasureValue.LOWER_BACK -> UR.color.chartLowerBack
        MeasureValue.CALF -> UR.color.chartCalf
        MeasureValue.BODY_FAT -> UR.color.chartBodyFat
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

fun MeasureData.displayValue(
    measureValue: MeasureValue,
    userSettings: UserSettingsData
) = measureValue.displayValue(this, userSettings)

fun MeasureValue.displayValue(
    measureData: MeasureData,
    userSettings: UserSettingsData
): Double =
    when (this) {
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
        MeasureValue.BODY_WEIGHT -> userSettings.displayWeight(measureData.bodyWeight)
        MeasureValue.BODY_FAT -> measureData.fatPercent
    }


fun MeasureData.calculateIntPart(
    progress: Int,
    measureValue: MeasureValue,
    userSettings: UserSettingsData
): Double {
    val currentValue = displayValue(measureValue, userSettings)
    val decimal = currentValue - currentValue.toInt()
    return progress.toDouble() + decimal
}


fun MeasureData.calculateDecimalPart(
    progress: Int,
    measureValue: MeasureValue,
    userSettings: UserSettingsData
): Double {
    val currentValue = displayValue(measureValue, userSettings).toInt()

    return currentValue.toDouble() + (progress.toDouble() / 10.0)
}
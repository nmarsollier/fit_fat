package com.nmarsollier.fitfat.ui.common

import com.nmarsollier.fitfat.R
import com.nmarsollier.fitfat.model.measures.MeasureMethod
import com.nmarsollier.fitfat.model.measures.MeasureValue
import com.nmarsollier.fitfat.model.userSettings.UserSettingsEntity
import com.nmarsollier.fitfat.model.userSettings.UserSettingsEntity.MeasureType
import com.nmarsollier.fitfat.utils.toInch
import com.nmarsollier.fitfat.utils.toPounds

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

val MeasureType.heightResId: Int
    get() = when (this) {
        MeasureType.METRIC -> R.string.unit_cm
        MeasureType.IMPERIAL -> R.string.unit_in
    }
val MeasureType.weightResId: Int
    get() = when (this) {
        MeasureType.METRIC -> R.string.unit_kg
        MeasureType.IMPERIAL -> R.string.unit_lb
    }

fun UserSettingsEntity?.displayWeight(weight: Double) =
    this?.measureSystem?.let {
        if (it == UserSettingsEntity.MeasureType.IMPERIAL) {
            weight.toPounds()
        } else {
            weight
        }
    } ?: weight


fun UserSettingsEntity?.displayHeight(value: Double) =
    this?.measureSystem?.let {
        if (it == UserSettingsEntity.MeasureType.IMPERIAL) {
            value.toInch()
        } else {
            value
        }
    } ?: value


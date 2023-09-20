package com.nmarsollier.fitfat.userSettings.ui.utils

import com.nmarsollier.fitfat.userSettings.R
import com.nmarsollier.fitfat.userSettings.model.db.UserSettingsData
import com.nmarsollier.fitfat.utils.converters.toInch
import com.nmarsollier.fitfat.utils.converters.toPounds

val UserSettingsData.MeasureType.heightResId: Int
    get() = when (this) {
        UserSettingsData.MeasureType.METRIC -> R.string.unit_cm
        UserSettingsData.MeasureType.IMPERIAL -> R.string.unit_in
    }

val UserSettingsData.MeasureType.weightResId: Int
    get() = when (this) {
        UserSettingsData.MeasureType.METRIC -> R.string.unit_kg
        UserSettingsData.MeasureType.IMPERIAL -> R.string.unit_lb
    }

fun UserSettingsData.displayWeight(weight: Double = this.weight) = this.measureSystem.let {
    if (it == UserSettingsData.MeasureType.IMPERIAL) {
        weight.toPounds()
    } else {
        weight
    }
}

fun UserSettingsData.displayHeight(height: Double = this.height) = this.measureSystem.let {
    if (it == UserSettingsData.MeasureType.IMPERIAL) {
        height.toInch()
    } else {
        height
    }
}

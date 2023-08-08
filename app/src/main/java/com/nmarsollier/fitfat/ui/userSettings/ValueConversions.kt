package com.nmarsollier.fitfat.ui.userSettings

import com.nmarsollier.fitfat.R
import com.nmarsollier.fitfat.utils.toInch
import com.nmarsollier.fitfat.utils.toPounds
import com.nmarsollier.fitfat.models.userSettings.UserSettings
import com.nmarsollier.fitfat.models.userSettings.db.MeasureType

inline val MeasureType.heightResId: Int
    get() = when (this) {
        MeasureType.METRIC -> R.string.unit_cm
        MeasureType.IMPERIAL -> R.string.unit_in
    }

inline val MeasureType.weightResId: Int
    get() = when (this) {
        MeasureType.METRIC -> R.string.unit_kg
        MeasureType.IMPERIAL -> R.string.unit_lb
    }

fun UserSettings.displayWeight(weight: Double = this.weight) = this.measureSystem.let {
    if (it == MeasureType.IMPERIAL) {
        weight.toPounds
    } else {
        weight
    }
}

fun UserSettings.displayHeight(height: Double = this.height) = this.measureSystem.let {
    if (it == MeasureType.IMPERIAL) {
        height.toInch
    } else {
        height
    }
}

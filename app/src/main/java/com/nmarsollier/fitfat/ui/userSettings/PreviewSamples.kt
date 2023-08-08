package com.nmarsollier.fitfat.ui.userSettings

import com.nmarsollier.fitfat.utils.dateOf
import com.nmarsollier.fitfat.models.userSettings.UserSettings
import com.nmarsollier.fitfat.models.userSettings.db.MeasureType
import com.nmarsollier.fitfat.models.userSettings.db.SexType

interface UserSettingsSamples {
    val simpleData: UserSettings
}

val UserSettings.Companion.Samples
    get() = object : UserSettingsSamples {
        override val simpleData = UserSettings(
            uid = 0,
            displayName = "Nestor Marsollier",
            birthDate = dateOf(2000, 12, 14),
            weight = 80.0,
            height = 183.0,
            sex = SexType.FEMALE,
            measureSystem = MeasureType.METRIC,
            firebaseToken = null
        )
    }

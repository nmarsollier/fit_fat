package com.nmarsollier.fitfat.userSettings.samples

import com.nmarsollier.fitfat.common.converters.dateOf
import com.nmarsollier.fitfat.common.ui.viewModel.Reducer
import com.nmarsollier.fitfat.userSettings.model.UserSettings
import com.nmarsollier.fitfat.userSettings.model.asUserSettings
import com.nmarsollier.fitfat.userSettings.model.db.UserSettingsData
import com.nmarsollier.fitfat.userSettings.ui.OptionsEvent
import com.nmarsollier.fitfat.userSettings.ui.OptionsView

interface UserSettingsSamples {
    val simpleData: UserSettings
}

val UserSettings.Companion.Samples
    get() = object : UserSettingsSamples {
        override val simpleData = UserSettingsData(
            uid = 0,
            displayName = "Nestor Marsollier",
            birthDate = dateOf(2000, 12, 14),
            weight = 80.0,
            height = 183.0,
            sex = UserSettingsData.SexType.FEMALE,
            measureSystem = UserSettingsData.MeasureType.METRIC,
            firebaseToken = null
        ).asUserSettings
    }

interface OptionsViewModelSamples {
    fun reducer(): Reducer<OptionsEvent>
}

val OptionsView.Companion.Samples: OptionsViewModelSamples
    get() = object : OptionsViewModelSamples {
        override fun reducer() = object : Reducer<OptionsEvent> {
            override fun reduce(event: OptionsEvent) = Unit
        }
    }

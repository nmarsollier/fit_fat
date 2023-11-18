package com.nmarsollier.fitfat.userSettings.ui.utils.preview

import androidx.activity.ComponentActivity
import com.nmarsollier.fitfat.userSettings.model.UserSettings
import com.nmarsollier.fitfat.userSettings.model.asUserSettings
import com.nmarsollier.fitfat.userSettings.model.db.UserSettingsData
import com.nmarsollier.fitfat.userSettings.ui.options.OptionsReducer
import com.nmarsollier.fitfat.userSettings.ui.options.OptionsViewModel
import com.nmarsollier.fitfat.utils.converters.dateOf
import java.util.Date

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
    fun reducer(): OptionsReducer
}

val OptionsViewModel.Companion.Samples: OptionsViewModelSamples
    get() = object : OptionsViewModelSamples {
        override fun reducer() = object : OptionsReducer {
            override fun loginWithGoogle(activity: ComponentActivity) = Unit

            override fun disableFirebase() = Unit

            override fun updateSex(newSex: UserSettingsData.SexType) = Unit

            override fun updateMeasureSystem(system: UserSettingsData.MeasureType) = Unit

            override fun updateWeight(newWeight: Double) = Unit

            override fun updateHeight(newHeight: Double) = Unit

            override fun updateDisplayName(newName: String) = Unit

            override fun updateBirthDate(newBirthDate: Date) = Unit

            override fun saveSettings() = Unit

            override fun load() = Unit

        }
    }

package com.nmarsollier.fitfat.measures.model

import com.nmarsollier.fitfat.userSettings.model.UserSettings
import com.nmarsollier.fitfat.userSettings.model.UserSettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext

class SaveMeasureAndUserSettingsService(
    private val measuresRepository: MeasuresRepository,
    private val userSettingsRepository: UserSettingsRepository
) {

    suspend fun saveMeasure(measure: Measure, userSettings: UserSettings) = coroutineScope {
        if (measure.isValid()) {
            withContext(Dispatchers.IO) {
                userSettings.updateWeight(measure.value.bodyWeight)

                measure.updateCloudSync(false)
                measuresRepository.update(measure)

                if (measure.value.bodyWeight > 0) {
                    userSettingsRepository.update(userSettings)
                }
            }

            true
        } else {
            false
        }
    }
}

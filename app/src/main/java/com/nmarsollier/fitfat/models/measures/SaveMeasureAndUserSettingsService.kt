package com.nmarsollier.fitfat.models.measures

import com.nmarsollier.fitfat.models.userSettings.UserSettings
import com.nmarsollier.fitfat.models.userSettings.UserSettingsRepository
import com.nmarsollier.fitfat.models.userSettings.updateWeight
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext

class SaveMeasureAndUserSettingsService(
    private val measuresRepository: MeasuresRepository,
    private val userSettingsRepository: UserSettingsRepository
) {

    suspend fun saveMeasure(measure: Measure, userSettings: UserSettings) =
        coroutineScope {
            if (measure.isValid) {
                withContext(Dispatchers.IO) {
                    if (measure.bodyWeight > 0) {
                        userSettingsRepository.update(userSettings.updateWeight(measure.bodyWeight))
                    }
                    measuresRepository.update(measure.updateCloudSync(false))
                }

                true
            } else {
                false
            }
        }
}

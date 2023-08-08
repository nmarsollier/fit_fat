package com.nmarsollier.fitfat.useCases

import com.google.firebase.firestore.FirebaseFirestore
import com.nmarsollier.fitfat.model.firebase.FirebaseDao
import com.nmarsollier.fitfat.model.measures.Measure
import com.nmarsollier.fitfat.model.measures.MeasuresRepository
import com.nmarsollier.fitfat.utils.logger
import com.nmarsollier.fitfat.utils.toIso8601
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class UpdatePendingMeasuresUseCase(
    private val firebaseDao: FirebaseDao,
    private val firebaseUseCase: FirebaseUseCase,
    private val measuresRepository: MeasuresRepository
) {
    fun update(measure: Measure) {
        firebaseUseCase.uploadPendingMeasures()
        uploadPendingMeasures()
    }

    private fun uploadPendingMeasures() = MainScope().launch(Dispatchers.IO) {

        val key = firebaseDao.userKey ?: return@launch

        val instance = FirebaseFirestore.getInstance()

        measuresRepository.findUnsynced().let {
            it?.forEach { measure ->
                instance.collection("measures").document(measure.uid).set(
                    mapOf(
                        "user" to key,
                        "bodyWeight" to measure.bodyWeight,
                        "bodyHeight" to measure.bodyHeight,
                        "age" to measure.age,
                        "sex" to measure.sex.toString(),
                        "date" to measure.date.toIso8601(),
                        "measureMethod" to measure.measureMethod.toString(),
                        "chest" to measure.chest,
                        "abdominal" to measure.abdominal,
                        "thigh" to measure.thigh,
                        "tricep" to measure.tricep,
                        "subscapular" to measure.subscapular,
                        "suprailiac" to measure.suprailiac,
                        "midaxillary" to measure.midaxillary,
                        "bicep" to measure.bicep,
                        "lowerBack" to measure.lowerBack,
                        "calf" to measure.calf,
                        "fatPercent" to measure.fatPercent
                    )
                ).addOnCompleteListener { status ->
                    MainScope().launch(Dispatchers.IO) {
                        if (status.isSuccessful) {
                            measure.cloudSync = true
                            measuresRepository.update(measure)
                        } else {
                            logger.severe("Error saving UserSettings ${status.exception}")
                        }
                    }
                }
            }
        }
    }
}
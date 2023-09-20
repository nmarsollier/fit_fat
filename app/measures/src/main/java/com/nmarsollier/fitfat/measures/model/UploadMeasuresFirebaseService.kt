package com.nmarsollier.fitfat.measures.model

import com.nmarsollier.fitfat.firebase.FirebaseConnection
import com.nmarsollier.fitfat.measures.model.api.MeasuresFirebaseApi
import com.nmarsollier.fitfat.utils.logger.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

internal class UploadMeasuresFirebaseService(
    private val measuresFirebaseApi: MeasuresFirebaseApi,
    private val measuresRepository: MeasuresRepository,
    private val logger: Logger,
    private val firebaseConnection: FirebaseConnection
) {
    init {
        MainScope().launch {
            firebaseConnection.statusFlow.collect {
                if (it) {
                    uploadPendingMeasures()
                }
            }
        }
    }

    fun uploadPendingMeasures() = MainScope().launch(Dispatchers.IO) {
        val measures = measuresRepository.findUnSynced() ?: return@launch

        measuresFirebaseApi.update(measures)?.let { status ->
            if (status.isSuccessful) {
                measures.forEach { measure ->
                    measure.updateCloudSync(true)
                    measuresRepository.update(measure)
                }
            } else {
                logger.e("Error saving UserSettings", status.exception)
            }
        }
    }
}
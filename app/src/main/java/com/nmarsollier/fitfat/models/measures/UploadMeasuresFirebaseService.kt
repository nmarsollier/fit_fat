package com.nmarsollier.fitfat.models.measures

import com.nmarsollier.fitfat.utils.Logger
import com.nmarsollier.fitfat.models.firebase.FirebaseConnection
import com.nmarsollier.fitfat.models.measures.api.MeasuresFirebaseApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal class UploadMeasuresFirebaseService(
    private val measuresFirebaseApi: MeasuresFirebaseApi,
    private val measuresRepository: MeasuresRepository,
    private val logger: Logger,
    private val firebaseConnection: FirebaseConnection
) {
    init {
        CoroutineScope(Dispatchers.IO).launch {
            firebaseConnection.statusFlow.collect {
                if (it) {
                    uploadPendingMeasures()
                }
            }
        }
    }

    private fun uploadPendingMeasures() = CoroutineScope(Dispatchers.IO).launch {
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
package com.nmarsollier.fitfat.model.measures

import com.google.firebase.firestore.QueryDocumentSnapshot
import com.nmarsollier.fitfat.model.db.getRoomDatabase
import com.nmarsollier.fitfat.model.firebase.FirebaseRepository
import com.nmarsollier.fitfat.model.userSettings.SexType
import com.nmarsollier.fitfat.model.userSettings.UserSettings
import com.nmarsollier.fitfat.utils.parseIso8601
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import java.util.Date

object MeasuresRepository {
    fun loadAll(): Flow<List<Measure>> = channelFlow {
        send(getRoomDatabase().measureDao().findAll())
    }

    fun findLast(): Flow<Measure?> = channelFlow {
        send(getRoomDatabase().measureDao().findLast())
    }

    fun update(measure: Measure) {
        measure.cloudSync = false
        getRoomDatabase().measureDao().update(measure)
        FirebaseRepository.uploadPendingMeasures()
    }

    fun findUnsynced(): Flow<List<Measure>?> = channelFlow {
        send(getRoomDatabase().measureDao().findUnsynced())
    }

    private fun findById(id: String): Flow<Measure?> = channelFlow {
        send(getRoomDatabase().measureDao().findById(id))
    }

    fun delete(
        measure: Measure
    ) {
        getRoomDatabase().measureDao().delete(measure)
        FirebaseRepository.deleteMeasure(measure)
    }

    fun updateFromFirebase(
        userSettings: UserSettings,
        document: QueryDocumentSnapshot?
    ) = MainScope().launch(Dispatchers.IO) {
        document ?: return@launch
        getRoomDatabase().measureDao().let { dao ->
            findById(document.id).collect {
                dao.insert(Measure.newMeasure(document.id).apply {
                    bodyWeight = document.getDouble("bodyWeight") ?: 0.0
                    fatPercent = document.getDouble("fatPercent") ?: 0.0
                    bodyHeight = document.getDouble("bodyHeight") ?: userSettings.height
                    age = (document.getDouble("age") ?: 0.0).toInt()
                    sex = SexType.valueOf(
                        document.getString("sex") ?: SexType.MALE.toString()
                    )
                    age = (document.getDouble("calf") ?: 0.0).toInt()
                    measureMethod =
                        MeasureMethod.valueOf(
                            document.getString("measureMethod")
                                ?: MeasureMethod.WEIGHT_ONLY.toString()
                        )
                    chest = (document.getDouble("chest") ?: 0.0).toInt()
                    abdominal = (document.getDouble("abdominal") ?: 0.0).toInt()
                    thigh = (document.getDouble("thigh") ?: 0.0).toInt()
                    tricep = (document.getDouble("tricep") ?: 0.0).toInt()
                    subscapular = (document.getDouble("subscapular") ?: 0.0).toInt()
                    suprailiac = (document.getDouble("suprailiac") ?: 0.0).toInt()
                    midaxillary = (document.getDouble("midaxillary") ?: 0.0).toInt()
                    bicep = (document.getDouble("bicep") ?: 0.0).toInt()
                    lowerBack = (document.getDouble("lowerBack") ?: 0.0).toInt()
                    date = document.getString("date")?.parseIso8601() ?: Date()
                    cloudSync = true
                })
            }
        }
    }

    fun insert(measure: Measure) = MainScope().launch(Dispatchers.IO) {
        getRoomDatabase().measureDao().insert(measure)
        MainScope().launch(Dispatchers.IO) {
            FirebaseRepository.uploadPendingMeasures()
        }
    }
}
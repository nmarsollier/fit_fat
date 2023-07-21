package com.nmarsollier.fitfat.model.measures

import android.content.Context
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.nmarsollier.fitfat.model.db.getRoomDatabase
import com.nmarsollier.fitfat.model.firebase.FirebaseRepository
import com.nmarsollier.fitfat.model.userSettings.SexType
import com.nmarsollier.fitfat.model.userSettings.UserSettings
import com.nmarsollier.fitfat.utils.parseIso8601
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import java.util.*

object MeasuresRepository {
    fun loadAll(context: Context): Flow<List<Measure>> = channelFlow {
        getRoomDatabase(context).measureDao().findAll().collect {
            send(it)
        }
    }

    fun findLast(context: Context): Flow<Measure?> = channelFlow {
        getRoomDatabase(context).measureDao().findLast().collect {
            send(it)
        }
    }

    fun update(context: Context, measure: Measure) = GlobalScope.launch(Dispatchers.IO) {
        measure.cloudSync = false
        getRoomDatabase(context).measureDao().update(measure)
        FirebaseRepository.uploadPendingMeasures(context)
    }

    fun findUnsynced(context: Context): Flow<List<Measure>?> = channelFlow {
        getRoomDatabase(context).measureDao().findUnsynced().collect {
            send(it)
            close()
        }
    }

    fun findById(context: Context, id: String): Flow<Measure?> = channelFlow {
        getRoomDatabase(context).measureDao().findById(id).collect {
            send(it)
            close()
        }
    }

    fun delete(
        context: Context, measure: Measure
    ) = GlobalScope.launch(Dispatchers.IO) {
        getRoomDatabase(context).measureDao().delete(measure)
        FirebaseRepository.deleteMeasure(measure)
    }

    fun updateFromFirebase(
        context: Context,
        userSettings: UserSettings,
        document: QueryDocumentSnapshot?
    ) = GlobalScope.launch(Dispatchers.IO) {
        document ?: return@launch
        getRoomDatabase(context).measureDao().let { dao ->
            findById(context, document.id).collect {
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

    fun insert(context: Context, measure: Measure) = GlobalScope.launch(Dispatchers.IO) {
        getRoomDatabase(context).measureDao().insert(measure)
        FirebaseRepository.uploadPendingMeasures(context)
    }
}
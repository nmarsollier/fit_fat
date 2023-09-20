package com.nmarsollier.fitfat.measures.model.api

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.nmarsollier.fitfat.firebase.FirebaseConnection
import com.nmarsollier.fitfat.measures.model.Measure
import com.nmarsollier.fitfat.measures.model.asMeasure
import com.nmarsollier.fitfat.measures.model.db.MeasureData
import com.nmarsollier.fitfat.measures.model.db.MeasureMethod
import com.nmarsollier.fitfat.userSettings.model.UserSettings
import com.nmarsollier.fitfat.userSettings.model.db.UserSettingsData
import com.nmarsollier.fitfat.utils.converters.parseIso8601
import com.nmarsollier.fitfat.utils.converters.toIso8601
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.util.Date
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MeasuresFirebaseApi internal constructor(
    private val firebaseConnection: FirebaseConnection
) {
    fun delete(measureId: String) {
        firebaseConnection.instance.collection("measures").document(measureId).delete()
    }

    suspend fun findAll(
        userSettings: UserSettings
    ): List<Measure>? {
        val key = firebaseConnection.userKey ?: return null

        val instance = firebaseConnection.instance
        val docRef = instance.collection("measures").whereEqualTo("user", key)

        return suspendCoroutine {
            docRef.get().addOnSuccessListener { documents ->
                MainScope().launch {
                    it.resume(documents?.map { it.toMeasureData(userSettings.value) })
                }
            }
        }
    }

    suspend fun update(
        measures: List<Measure>
    ): Task<Void>? = suspendCoroutine { suspended ->
        val key = firebaseConnection.userKey ?: run {
            suspended.resume(null)
            return@suspendCoroutine
        }
        val instance = firebaseConnection.instance

        measures.forEach {
            val measure = it.value
            instance.collection("measures")
                .document(measure.uid).set(
                    it.toDocumentMap(key)
                ).addOnCompleteListener {
                    MainScope().launch(Dispatchers.Main) {
                        suspended.resume(it)
                    }
                }
        }
    }
}

private fun QueryDocumentSnapshot.toMeasureData(userSettingsData: UserSettingsData) =
    MeasureData(
        uid = id,
        bodyWeight = getDouble("bodyWeight") ?: 0.0,
        fatPercent = getDouble("fatPercent") ?: 0.0,
        bodyHeight = getDouble("bodyHeight") ?: userSettingsData.height,
        age = (getDouble("age") ?: 0.0).toInt(),
        sex = UserSettingsData.SexType.valueOf(
            getString("sex") ?: UserSettingsData.SexType.MALE.toString()
        ),
        calf = (getDouble("calf") ?: 0.0).toInt(),
        measureMethod = MeasureMethod.valueOf(
            getString("measureMethod") ?: MeasureMethod.WEIGHT_ONLY.toString()
        ),
        chest = (getDouble("chest") ?: 0.0).toInt(),
        abdominal = (getDouble("abdominal") ?: 0.0).toInt(),
        thigh = (getDouble("thigh") ?: 0.0).toInt(),
        tricep = (getDouble("tricep") ?: 0.0).toInt(),
        subscapular = (getDouble("subscapular") ?: 0.0).toInt(),
        suprailiac = (getDouble("suprailiac") ?: 0.0).toInt(),
        midaxillary = (getDouble("midaxillary") ?: 0.0).toInt(),
        bicep = (getDouble("bicep") ?: 0.0).toInt(),
        lowerBack = (getDouble("lowerBack") ?: 0.0).toInt(),
        date = getString("date")?.parseIso8601() ?: Date(),
        cloudSync = true
    ).asMeasure

private fun Measure.toDocumentMap(key: String) = mapOf(
    "user" to key,
    "bodyWeight" to value.bodyWeight,
    "bodyHeight" to value.bodyHeight,
    "age" to value.age,
    "sex" to value.sex.toString(),
    "date" to value.date.toIso8601(),
    "measureMethod" to value.measureMethod.toString(),
    "chest" to value.chest,
    "abdominal" to value.abdominal,
    "thigh" to value.thigh,
    "tricep" to value.tricep,
    "subscapular" to value.subscapular,
    "suprailiac" to value.suprailiac,
    "midaxillary" to value.midaxillary,
    "bicep" to value.bicep,
    "lowerBack" to value.lowerBack,
    "calf" to value.calf,
    "fatPercent" to value.fatPercent
)

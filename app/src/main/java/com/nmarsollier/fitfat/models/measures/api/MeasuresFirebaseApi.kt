package com.nmarsollier.fitfat.models.measures.api

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.nmarsollier.fitfat.utils.parseIso8601
import com.nmarsollier.fitfat.utils.toIso8601
import com.nmarsollier.fitfat.models.firebase.FirebaseConnection
import com.nmarsollier.fitfat.models.measures.Measure
import com.nmarsollier.fitfat.models.measures.db.MeasureMethod
import com.nmarsollier.fitfat.models.userSettings.UserSettings
import com.nmarsollier.fitfat.models.userSettings.db.SexType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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

        return suspendCoroutine { continuation ->
            docRef.get().addOnSuccessListener { documents ->
                continuation.resume(documents?.map { it.toMeasure(userSettings) })
            }
        }
    }

    suspend fun update(
        measures: List<Measure>
    ): Task<Void>? = suspendCoroutine { continuation ->
        val key = firebaseConnection.userKey ?: run {
            continuation.resume(null)
            return@suspendCoroutine
        }
        val instance = firebaseConnection.instance

        CoroutineScope(Dispatchers.IO).launch {
            measures.forEach { measure ->
                instance.collection("measures").document(measure.uid).set(
                    measure.toDocumentMap(key)
                ).addOnCompleteListener {
                    continuation.resume(it)
                }
            }
        }
    }
}


private fun QueryDocumentSnapshot.toMeasure(userSettingsData: UserSettings) = Measure(
    uid = id,
    bodyWeight = getDouble("bodyWeight") ?: 0.0,
    fatPercent = getDouble("fatPercent") ?: 0.0,
    bodyHeight = getDouble("bodyHeight") ?: userSettingsData.height,
    age = (getDouble("age") ?: 0.0).toInt(),
    sex = SexType.valueOf(
        getString("sex") ?: SexType.MALE.toString()
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
    date = getString("date")?.parseIso8601 ?: Date(),
    cloudSync = true
)

private fun Measure.toDocumentMap(key: String) = mapOf(
    "user" to key,
    "bodyWeight" to bodyWeight,
    "bodyHeight" to bodyHeight,
    "age" to age,
    "sex" to sex.toString(),
    "date" to date.toIso8601,
    "measureMethod" to measureMethod.toString(),
    "chest" to chest,
    "abdominal" to abdominal,
    "thigh" to thigh,
    "tricep" to tricep,
    "subscapular" to subscapular,
    "suprailiac" to suprailiac,
    "midaxillary" to midaxillary,
    "bicep" to bicep,
    "lowerBack" to lowerBack,
    "calf" to calf,
    "fatPercent" to fatPercent
)
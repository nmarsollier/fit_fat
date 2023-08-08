package com.nmarsollier.fitfat.models.userSettings.api

import android.os.Parcelable
import com.google.firebase.firestore.DocumentSnapshot
import com.nmarsollier.fitfat.utils.Logger
import com.nmarsollier.fitfat.utils.nullIfEmpty
import com.nmarsollier.fitfat.utils.parseIso8601
import com.nmarsollier.fitfat.utils.toIso8601
import com.nmarsollier.fitfat.models.firebase.FirebaseConnection
import com.nmarsollier.fitfat.models.userSettings.UserSettings
import com.nmarsollier.fitfat.models.userSettings.db.MeasureType
import com.nmarsollier.fitfat.models.userSettings.db.SexType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import java.util.Date
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class UserSettingsFirebaseApi internal constructor(
    private val firebaseConnection: FirebaseConnection,
    private val logger: Logger
) {
    fun update(userSettings: UserSettings) {
        val key = firebaseConnection.userKey ?: return

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val instance = firebaseConnection.instance
                instance.collection("settings").document(key).set(
                    mapOf(
                        "displayName" to userSettings.displayName,
                        "birthDate" to userSettings.birthDate.toIso8601,
                        "height" to userSettings.height,
                        "weight" to userSettings.weight,
                        "measureSystem" to userSettings.measureSystem.toString(),
                        "sex" to userSettings.sex.toString()
                    )
                ).addOnCompleteListener {
                    if (!it.isSuccessful) {
                        logger.e("Error saving UserSettings", it.exception)
                    }
                }
            } catch (e: Exception) {
                logger.e("Error saving UserSettings", e)
            }
        }
    }

    suspend fun findCurrent(): FirebaseUserSettingsData? = coroutineScope {
        val key = firebaseConnection.userKey ?: return@coroutineScope null

        val instance = firebaseConnection.instance
        val docRef = instance.collection("settings").document(key)
        suspendCoroutine { continuation ->
            docRef.get().addOnSuccessListener { document ->
                continuation.resume(document.toUserSettings())
            }.addOnFailureListener { exception ->
                logger.e(exception.message, exception)
                continuation.resume(null)
            }
        }
    }
}

@Parcelize
data class FirebaseUserSettingsData(
    var displayName: String?,
    var birthDate: Date?,
    var weight: Double?,
    var height: Double?,
    var sex: SexType?,
    var measureSystem: MeasureType?
) : Parcelable

private fun DocumentSnapshot.toUserSettings() =
    FirebaseUserSettingsData(displayName = getString("displayName").nullIfEmpty(),
        birthDate = this.getString("birthDate").nullIfEmpty()?.parseIso8601,
        weight = getDouble("weight"),
        height = getDouble("height"),
        sex = this.getString("sex").nullIfEmpty()?.let {
            SexType.valueOf(it)
        },
        measureSystem = getString("measureSystem").nullIfEmpty()?.let {
            MeasureType.valueOf(it)
        })

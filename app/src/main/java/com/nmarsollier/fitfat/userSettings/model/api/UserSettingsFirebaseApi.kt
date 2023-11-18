package com.nmarsollier.fitfat.userSettings.model.api

import android.os.Parcelable
import com.google.firebase.firestore.DocumentSnapshot
import com.nmarsollier.fitfat.firebase.FirebaseConnection
import com.nmarsollier.fitfat.userSettings.model.UserSettings
import com.nmarsollier.fitfat.userSettings.model.db.UserSettingsData.MeasureType
import com.nmarsollier.fitfat.userSettings.model.db.UserSettingsData.SexType
import com.nmarsollier.fitfat.utils.converters.nullIfEmpty
import com.nmarsollier.fitfat.utils.converters.parseIso8601
import com.nmarsollier.fitfat.utils.converters.toIso8601
import com.nmarsollier.fitfat.utils.logger.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import java.util.Date
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class UserSettingsFirebaseApi internal constructor(
    private val firebaseConnection: FirebaseConnection,
    private val logger: com.nmarsollier.fitfat.utils.logger.Logger
) {
    fun update(userSettings: UserSettings) {
        val key = firebaseConnection.userKey ?: return

        MainScope().launch(Dispatchers.IO) {
            try {
                val instance = firebaseConnection.instance
                instance.collection("settings").document(key).set(
                    mapOf(
                        "displayName" to userSettings.value.displayName,
                        "birthDate" to userSettings.value.birthDate.toIso8601(),
                        "height" to userSettings.value.height,
                        "weight" to userSettings.value.weight,
                        "measureSystem" to userSettings.value.measureSystem.toString(),
                        "sex" to userSettings.value.sex.toString()
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
        suspendCoroutine {
            docRef.get().addOnSuccessListener { document ->
                it.resume(document.toUserSettingsData())
            }.addOnFailureListener { exception ->
                logger.e(exception.message, exception)
                it.resume(null)
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

private fun DocumentSnapshot.toUserSettingsData() =
    FirebaseUserSettingsData(displayName = getString("displayName").nullIfEmpty(),
        birthDate = this.getString("birthDate").nullIfEmpty()?.parseIso8601(),
        weight = getDouble("weight"),
        height = getDouble("height"),
        sex = this.getString("sex").nullIfEmpty()?.let {
            SexType.valueOf(it)
        },
        measureSystem = getString("measureSystem").nullIfEmpty()?.let {
            MeasureType.valueOf(it)
        })

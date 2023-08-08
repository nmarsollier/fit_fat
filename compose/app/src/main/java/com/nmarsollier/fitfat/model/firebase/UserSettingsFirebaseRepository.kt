package com.nmarsollier.fitfat.model.firebase

import android.os.Parcelable
import com.google.firebase.firestore.DocumentSnapshot
import com.nmarsollier.fitfat.model.userSettings.UserSettingsEntity
import com.nmarsollier.fitfat.model.userSettings.UserSettingsEntity.MeasureType
import com.nmarsollier.fitfat.model.userSettings.UserSettingsEntity.SexType
import com.nmarsollier.fitfat.utils.logger
import com.nmarsollier.fitfat.utils.nullIfEmpty
import com.nmarsollier.fitfat.utils.parseIso8601
import com.nmarsollier.fitfat.utils.toIso8601
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import java.util.Date
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class UserSettingsFirebaseRepository(
    private val firebaseConnection: FirebaseConnection
) {
    fun save(userSettingsEntity: UserSettingsEntity) {
        val key = firebaseConnection.userKey ?: return

        MainScope().launch(Dispatchers.IO) {
            try {
                val instance = firebaseConnection.instance
                instance.collection("settings").document(key).set(
                    mapOf(
                        "displayName" to userSettingsEntity.displayName,
                        "birthDate" to userSettingsEntity.birthDate.toIso8601(),
                        "height" to userSettingsEntity.height,
                        "weight" to userSettingsEntity.weight,
                        "measureSystem" to userSettingsEntity.measureSystem.toString(),
                        "sex" to userSettingsEntity.sex.toString()
                    )
                ).addOnCompleteListener {
                    if (!it.isSuccessful) {
                        logger.severe("Error saving UserSettings ${it.exception}")
                    }
                }
            } catch (e: Exception) {
                logger.severe("Error saving UserSettings $e")
            }
        }
    }

    suspend fun load(): UserSettingsDTO? = coroutineScope {
        val key = firebaseConnection.userKey ?: return@coroutineScope null

        val instance = firebaseConnection.instance
        val docRef = instance.collection("settings").document(key)
        suspendCoroutine {
            docRef.get().addOnSuccessListener { document ->
                it.resume(document.toUserSettings())
            }.addOnFailureListener { exception ->
                logger.severe(exception.message)
                it.resume(null)
            }
        }
    }
}

@Parcelize
data class UserSettingsDTO(
    var displayName: String?,
    var birthDate: Date?,
    var weight: Double?,
    var height: Double?,
    var sex: SexType?,
    var measureSystem: MeasureType?
) : Parcelable

private fun DocumentSnapshot.toUserSettings() =
    UserSettingsDTO(displayName = getString("displayName").nullIfEmpty(),
        birthDate = this.getString("birthDate").nullIfEmpty()?.parseIso8601(),
        weight = getDouble("weight"),
        height = getDouble("height"),
        sex = this.getString("sex").nullIfEmpty()?.let {
            SexType.valueOf(it)
        },
        measureSystem = getString("measureSystem").nullIfEmpty()?.let {
            MeasureType.valueOf(it)
        })

package com.nmarsollier.fitfat.model.firebase

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.nmarsollier.fitfat.model.measures.MeasuresRepository
import com.nmarsollier.fitfat.model.userSettings.UserSettings
import com.nmarsollier.fitfat.utils.logger
import com.nmarsollier.fitfat.utils.runInBackground
import com.nmarsollier.fitfat.utils.toIso8601
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch

object FirebaseDao {
    private val logger by logger()
    private lateinit var auth: FirebaseAuth
    private var currentUser: FirebaseUser? = null

    fun init(): FirebaseAuth {
        return FirebaseAuth.getInstance().also {
            auth = it
        }
    }

    fun googleAuth(token: String): Flow<Boolean?> = channelFlow {
        val credential = GoogleAuthProvider.getCredential(token, null)
        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                currentUser = auth.currentUser
                launch {
                    send(true)
                    close()
                }
            } else {
                currentUser = null
                launch {
                    send(true)
                    close()
                }
            }
        }.addOnCanceledListener {
            launch {
                send(true)
                close()
            }
            currentUser = null
        }
        awaitClose()
    }

    fun uploadUserSettings(userSettings: UserSettings) {
        val key = getUserKey() ?: return

        runInBackground {
            try {
                val instance = FirebaseFirestore.getInstance()
                instance.collection("settings").document(key).set(
                    mapOf(
                        "displayName" to userSettings.displayName,
                        "birthDate" to userSettings.birthDate.toIso8601(),
                        "height" to userSettings.height,
                        "weight" to userSettings.weight,
                        "measureSystem" to userSettings.measureSystem.toString(),
                        "sex" to userSettings.sex.toString()
                    )
                )
            } catch (e: Exception) {
                logger.severe("Error saving UserSettings $e")
            }
        }
    }

    fun uploadPendingMeasures(context: Context) = GlobalScope.launch(Dispatchers.IO) {
        val key = getUserKey() ?: return@launch

        val instance = FirebaseFirestore.getInstance()

        MeasuresRepository.findUnsynced(context).collect {
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
                    if (status.isSuccessful) {
                        measure.cloudSync = true
                        MeasuresRepository.update(context, measure)
                    }
                }
            }
        }
    }


    fun deleteMeasure(measureId: String) {
        val instance = FirebaseFirestore.getInstance()
        instance.collection("measures").document(measureId).delete()
    }

    fun downloadUserSettings(): Flow<DocumentSnapshot?> = channelFlow {
        val key = getUserKey()
        if (key == null) {
            send(null)
            return@channelFlow
        }

        val instance = FirebaseFirestore.getInstance()
        val docRef = instance.collection("settings").document(key)
        docRef.get()
            .addOnSuccessListener { document ->
                launch {
                    send(document)
                }
            }
            .addOnFailureListener { exception ->
                logger.severe(exception.message)
                launch {
                    send(null)
                }
            }
    }

    fun downloadMeasurements(context: Context): Flow<QuerySnapshot?> = channelFlow {
        val key = getUserKey() ?: return@channelFlow

        val instance = FirebaseFirestore.getInstance()
        val docRef = instance.collection("measures").whereEqualTo("user", key)

        docRef.get()
            .addOnSuccessListener { documents ->
                launch {
                    send(documents)
                }
            }
    }

    private fun getUserKey(): String? {
        return currentUser?.uid
    }
}

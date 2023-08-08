package com.nmarsollier.fitfat.model.firebase

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.nmarsollier.fitfat.model.userSettings.UserSettings
import com.nmarsollier.fitfat.utils.logger
import com.nmarsollier.fitfat.utils.toIso8601
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FirebaseDao {
    private lateinit var auth: FirebaseAuth
    private var currentUser: FirebaseUser? = null

    internal val userKey: String?
        get() = currentUser?.uid

    internal fun init(): FirebaseAuth {
        return FirebaseAuth.getInstance().also {
            auth = it
        }
    }

    internal suspend fun signInWithCredentials(credential: AuthCredential): Boolean =
        coroutineScope {
            if (auth.currentUser != null) {
                currentUser = auth.currentUser
                return@coroutineScope true
            }

            suspendCoroutine {
                auth.signInWithCredential(credential).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        currentUser = auth.currentUser
                        it.resume(true)
                    } else {
                        currentUser = null
                        it.resume(true)
                    }
                }.addOnCanceledListener {
                    it.resume(true)
                    currentUser = null
                }
            }
        }

    internal fun uploadUserSettings(userSettings: UserSettings) {
        val key = userKey ?: return

        MainScope().launch(Dispatchers.IO) {
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

    internal fun deleteMeasure(measureId: String) {
        val instance = FirebaseFirestore.getInstance()
        instance.collection("measures").document(measureId).delete()
    }

    internal suspend fun downloadUserSettings(): DocumentSnapshot? = coroutineScope {
        val key = userKey ?: return@coroutineScope null

        val instance = FirebaseFirestore.getInstance()
        val docRef = instance.collection("settings").document(key)
        suspendCoroutine {
            docRef.get().addOnSuccessListener { document ->
                it.resume(document)
            }.addOnFailureListener { exception ->
                logger.severe(exception.message)
                it.resume(null)
            }
        }
    }

    internal suspend fun downloadMeasurements(): QuerySnapshot? = coroutineScope {
        val key = userKey ?: return@coroutineScope null

        val instance = FirebaseFirestore.getInstance()
        val docRef = instance.collection("measures").whereEqualTo("user", key)

        suspendCoroutine {
            docRef.get().addOnSuccessListener { documents ->
                launch {
                    it.resume(documents)
                }
            }
        }
    }
}

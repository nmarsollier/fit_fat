package com.nmarsollier.fitfat.model

import android.content.Context
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.nmarsollier.fitfat.R
import com.nmarsollier.fitfat.utils.*


object FirebaseDao {
    private lateinit var gso: GoogleSignInOptions
    private lateinit var auth: FirebaseAuth
    private var currentUser: FirebaseUser? = null

    fun init(context: Context) {
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        auth = FirebaseAuth.getInstance()

        runInBackground {
            getRoomDatabase(context).userDao().getUserSettings().firebaseToken?.let {
                runInForeground {
                    firebaseAuthWithGoogle(it) {}
                }
            }
        }
    }

    fun login(fragment: Fragment) {
        val context = fragment.context ?: return

        val googleApiClient = GoogleApiClient.Builder(context)
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build()

        val signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient)
        fragment.startActivityForResult(signInIntent, ResultCodes.RC_SIGN_IN.code)
    }

    fun firebaseAuthWithGoogle(token: String, callback: () -> Unit) {
        val credential = GoogleAuthProvider.getCredential(token, null)
        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                currentUser = auth.currentUser
                callback.invoke()
            } else {
                currentUser = null
            }
        }
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
                logError("Error saving UserSettings ", e)
            }
        }
    }

    fun uploadPendingMeasures(context: Context) {
        val key = getUserKey() ?: return

        runInBackground {
            val instance = FirebaseFirestore.getInstance()
            val dao = getRoomDatabase(context).measureDao()

            dao.getMeasuresToSync()?.forEach { measure ->
                instance.collection("measures").document(measure.uid).set(
                    mapOf(
                        "user" to key,
                        "bodyWeight" to measure.bodyWeight,
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
                        runInBackground {
                            measure.cloudSync = true
                            dao.update(measure)
                        }
                    }
                }
            }
        }
    }


    fun deleteMeasure(context: Context, measureId: String) {
        val instance = FirebaseFirestore.getInstance()
        instance.collection("measures").document(measureId).delete()
    }

    fun downloadUserSettings(context: Context, token: String, callback: () -> Unit) {
        val key = getUserKey() ?: return

        val instance = FirebaseFirestore.getInstance()
        val docRef = instance.collection("settings").document(key)
        docRef.get()
            .addOnSuccessListener { document ->
                runInBackground {
                    val dao = getRoomDatabase(context).userDao()
                    val userSettings = dao.getUserSettings()
                    userSettings.birthDate = document.getString("birthDate")?.parseIso8601() ?: userSettings.birthDate
                    userSettings.displayName = document.getString("displayName") ?: userSettings.displayName
                    userSettings.height = document.getDouble("height") ?: userSettings.height
                    userSettings.weight = document.getDouble("weight") ?: userSettings.weight
                    userSettings.measureSystem = MeasureType.valueOf(
                        document.getString("measureSystem") ?: userSettings.measureSystem.toString()
                    )
                    userSettings.sex = SexType.valueOf(
                        document.getString("sex") ?: userSettings.sex.toString()
                    )
                    userSettings.firebaseToken = token

                    dao.update(userSettings)
                    callback.invoke()
                }
            }
            .addOnFailureListener { exception ->
                logError("get failed with ", exception)
            }
    }

    fun downloadMeasurements(context: Context) {
        val key = getUserKey() ?: return

        val instance = FirebaseFirestore.getInstance()
        val docRef = instance.collection("measures").whereEqualTo("user", key)
        val dao = getRoomDatabase(context).measureDao()
        docRef.get()
            .addOnSuccessListener { documents ->
                documents.forEach { document ->
                    runInBackground {
                        if (dao.getById(document.id) == null) {
                            val measure = Measure(document.id, 0.0, 0, SexType.MALE)
                            measure.bodyWeight = document.getDouble("bodyWeight") ?: 0.0
                            measure.fatPercent = document.getDouble("fatPercent") ?: 0.0
                            measure.age = (document.getDouble("age") ?: 0.0).toInt()
                            measure.sex = SexType.valueOf(
                                document.getString("sex") ?: measure.sex.toString()
                            )
                            measure.age = (document.getDouble("calf") ?: 0.0).toInt()
                            measure.measureMethod =
                                MeasureMethod.valueOf(
                                    document.getString("measureMethod") ?: MeasureMethod.WEIGHT_ONLY.toString()
                                )
                            measure.chest = (document.getDouble("chest") ?: 0.0).toInt()
                            measure.abdominal = (document.getDouble("abdominal") ?: 0.0).toInt()
                            measure.thigh = (document.getDouble("thigh") ?: 0.0).toInt()
                            measure.tricep = (document.getDouble("tricep") ?: 0.0).toInt()
                            measure.subscapular = (document.getDouble("subscapular") ?: 0.0).toInt()
                            measure.suprailiac = (document.getDouble("suprailiac") ?: 0.0).toInt()
                            measure.midaxillary = (document.getDouble("midaxillary") ?: 0.0).toInt()
                            measure.bicep = (document.getDouble("bicep") ?: 0.0).toInt()
                            measure.lowerBack = (document.getDouble("lowerBack") ?: 0.0).toInt()
                            measure.date = document.getString("date")?.parseIso8601() ?: measure.date
                            measure.cloudSync = true

                            dao.insert(measure)
                        }
                    }
                }
                uploadPendingMeasures(context)
            }
            .addOnFailureListener { exception ->
                logError("get failed with ", exception)
            }
    }


    private fun getUserKey(): String? {
        return currentUser?.uid
    }
}

package com.nmarsollier.fitfat.model.firebase

import android.os.Parcelable
import com.google.firebase.auth.AuthCredential
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.nmarsollier.fitfat.model.measures.Measure
import com.nmarsollier.fitfat.model.measures.MeasureMethod
import com.nmarsollier.fitfat.model.userSettings.MeasureType
import com.nmarsollier.fitfat.model.userSettings.SexType
import com.nmarsollier.fitfat.model.userSettings.UserSettings
import com.nmarsollier.fitfat.model.userSettings.UserSettingsRepository
import com.nmarsollier.fitfat.useCases.FirebaseUseCase
import com.nmarsollier.fitfat.utils.nullIfEmpty
import com.nmarsollier.fitfat.utils.parseIso8601
import com.nmarsollier.fitfat.utils.toIso8601
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import java.util.Date

class FirebaseRepository(
    private val firebaseDao: FirebaseDao, private val userSettingsRepository: UserSettingsRepository
) {
    /**
     * Intializes the library checking in the user is already logged, in
     */
    fun checkAlreadyLoggedIn(firebaseUseCases: FirebaseUseCase) =
        MainScope().launch(Dispatchers.IO) {
            firebaseDao.init().let {
                userSettingsRepository.load().let {
                    it.firebaseToken?.let { token ->
                        firebaseUseCases.signInWithGoogle(token)
                    }
                }
            }
        }

    suspend fun signInWithCredential(token: AuthCredential): Boolean =
        firebaseDao.signInWithCredentials(token)

    internal fun uploadPendingMeasures(
        measures: List<Measure>
    ) = channelFlow {
        val key = firebaseDao.userKey ?: run {
            send(null)
            return@channelFlow
        }
        val instance = FirebaseFirestore.getInstance()

        measures.forEach { measure ->
            instance.collection("measures").document(measure.uid).set(
                measure.toDocumentMap(key)
            ).addOnCompleteListener { status ->
                MainScope().launch(Dispatchers.Main) {
                    send(status)
                }
            }
        }
        awaitClose()
    }

    fun uploadUserSettings(userSettings: UserSettings) {
        firebaseDao.uploadUserSettings(userSettings)
    }

    fun deleteMeasure(measure: Measure) {
        firebaseDao.deleteMeasure(measure.uid)
    }

    suspend fun downloadMeasures(): List<Measure>? = coroutineScope {
        val userSettings = userSettingsRepository.load()
        firebaseDao.downloadMeasurements()?.let { data ->
            data?.map { document -> document.toMeasure(userSettings) }
        }
    }

    suspend fun downloadUserSettings() = coroutineScope {
        firebaseDao.downloadUserSettings()?.toUserSettings()
    }

    suspend fun save(userSettings: UserSettings) = coroutineScope {
        uploadUserSettings(userSettings)
        userSettings
    }
}

private fun QueryDocumentSnapshot.toMeasure(userSettings: UserSettings) = Measure(
    uid = id,
    bodyWeight = getDouble("bodyWeight") ?: 0.0,
    fatPercent = getDouble("fatPercent") ?: 0.0,
    bodyHeight = getDouble("bodyHeight") ?: userSettings.height,
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
    date = getString("date")?.parseIso8601() ?: Date(),
    cloudSync = true
)

private fun Measure.toDocumentMap(key: String) = mapOf(
    "user" to key,
    "bodyWeight" to bodyWeight,
    "bodyHeight" to bodyHeight,
    "age" to age,
    "sex" to sex.toString(),
    "date" to date.toIso8601(),
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

@Parcelize
data class UserSettingsDto(
    var displayName: String?,
    var birthDate: Date?,
    var weight: Double?,
    var height: Double?,
    var sex: SexType?,
    var measureSystem: MeasureType?
) : Parcelable

private fun DocumentSnapshot.toUserSettings() =
    UserSettingsDto(displayName = getString("displayName").nullIfEmpty(),
        birthDate = this.getString("birthDate").nullIfEmpty()?.parseIso8601(),
        weight = getDouble("weight"),
        height = getDouble("height"),
        sex = this.getString("sex").nullIfEmpty()?.let {
            SexType.valueOf(it)
        },
        measureSystem = getString("measureSystem").nullIfEmpty()?.let {
            MeasureType.valueOf(it)
        })

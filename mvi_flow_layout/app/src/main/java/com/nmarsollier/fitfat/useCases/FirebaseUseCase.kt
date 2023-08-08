package com.nmarsollier.fitfat.useCases

import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.GoogleAuthProvider
import com.nmarsollier.fitfat.model.firebase.FirebaseRepository
import com.nmarsollier.fitfat.model.measures.MeasuresRepository
import com.nmarsollier.fitfat.model.userSettings.UserSettingsRepository
import com.nmarsollier.fitfat.ui.options.OptionsFragment
import com.nmarsollier.fitfat.utils.logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FirebaseUseCase(
    private val userSettingsRepository: UserSettingsRepository,
    private val measuresRepository: MeasuresRepository,
    private val firebaseRepository: FirebaseRepository,
    private val googleUseCase: GoogleUseCase
) {
    suspend fun signInWithGoogle(googleToken: String): Boolean? = coroutineScope {
        val credential = GoogleAuthProvider.getCredential(googleToken, null)
        firebaseRepository.signInWithCredential(credential)
    }

    /**
     * Login and sync firebase using google
     */
    suspend fun googleLoginAndSync(fragment: OptionsFragment): GoogleLoginResult = coroutineScope {
        val googleResult = googleUseCase.login(fragment)
        suspendCoroutine {
            when (googleResult) {
                is GoogleLoginResult.Error -> {
                    it.resume(googleResult)
                    userSettingsRepository.updateFirebaseToken(null)
                }

                is GoogleLoginResult.Success -> {
                    fragment.lifecycleScope.launch {
                        signInWithGoogle(googleResult.token).let { fbResult ->
                            if (fbResult == true) {
                                userSettingsRepository.updateFirebaseToken(googleResult.token)
                                    .join()
                                downloadAndSyncUserSettings().join()
                                downloadAndSyncMeasures().join()
                                uploadPendingMeasures().join()
                                it.resume(googleResult)
                            } else {
                                it.resume(GoogleLoginResult.Error(Exception("FirebaseRepository.googleAuth error")))
                            }
                        }
                    }
                }
            }
        }
    }

    private fun downloadAndSyncMeasures() = MainScope().launch(Dispatchers.IO) {
        firebaseRepository.downloadMeasures().let { measures ->
            measures?.forEach { measure ->
                measuresRepository.updateFromFirebase(measure)
            }
        }
    }

    private fun downloadAndSyncUserSettings() = MainScope().launch(Dispatchers.IO) {
        firebaseRepository.downloadUserSettings().let {
            it?.let {
                userSettingsRepository.updateFromFirebase(it)
            }
        }
    }

    fun uploadPendingMeasures() = MainScope().launch(Dispatchers.IO) {
        val measures = measuresRepository.findUnsynced() ?: return@launch

        firebaseRepository.uploadPendingMeasures(measures).filterNotNull().collect { status ->
            if (status.isSuccessful) {
                measures.forEach { measure ->
                    measure.cloudSync = true
                    measuresRepository.update(measure)
                }
            } else {
                logger.severe("Error saving UserSettings ${status?.exception}")
            }
        }
    }
}

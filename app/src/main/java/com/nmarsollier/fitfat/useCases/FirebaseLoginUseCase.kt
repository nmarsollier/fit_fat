package com.nmarsollier.fitfat.useCases

import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.GoogleAuthProvider
import com.nmarsollier.fitfat.model.firebase.FirebaseConnection
import com.nmarsollier.fitfat.model.firebase.GoogleAuth
import com.nmarsollier.fitfat.model.firebase.GoogleAuthResult
import com.nmarsollier.fitfat.model.userSettings.UserSettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FirebaseLoginUseCase(
    private val userSettingsRepository: UserSettingsRepository,
    private val firebaseConnection: FirebaseConnection,
    private val googleAuth: GoogleAuth,
    private val firebaseMeasuresSyncUseCase: FirebaseMeasuresSyncUseCase,
    private val firebaseSettingsSyncUseCase: FirebaseSettingsSyncUseCase
) {
    suspend fun signInWithGoogle(googleToken: String): Boolean {
        val credential = GoogleAuthProvider.getCredential(googleToken, null)
        return firebaseConnection.signInWithCredentials(credential)
    }

    /**
     * Login and sync firebase using google
     */
    suspend fun googleLoginAndSync(activity: ComponentActivity): GoogleAuthResult {
        val googleResult = googleAuth.login(activity)
        return suspendCoroutine {
            when (googleResult) {
                is GoogleAuthResult.Error -> {
                    it.resume(googleResult)
                    MainScope().launch(Dispatchers.IO) {
                        userSettingsRepository.updateFirebaseToken(null)
                    }
                }

                is GoogleAuthResult.Success -> {
                    activity.lifecycleScope.launch {
                        signInWithGoogle(googleResult.token).let { fbResult ->
                            if (fbResult) {
                                launch(Dispatchers.IO) {
                                    userSettingsRepository.updateFirebaseToken(googleResult.token)
                                    firebaseSettingsSyncUseCase.downloadAndSyncUserSettings()
                                    firebaseMeasuresSyncUseCase.downloadAndSyncMeasures()
                                    firebaseMeasuresSyncUseCase.uploadPendingMeasures()
                                }.join()
                                it.resume(googleResult)
                            } else {
                                it.resume(GoogleAuthResult.Error)
                            }
                        }
                    }
                }
            }
        }
    }
}
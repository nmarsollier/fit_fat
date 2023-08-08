package com.nmarsollier.fitfat.model.firebase

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.nmarsollier.fitfat.model.userSettings.UserSettingsEntity
import com.nmarsollier.fitfat.useCases.FirebaseLoginUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FirebaseConnection {
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    val userKey: String?
        get() = auth.currentUser?.uid

    val instance
        get() = FirebaseFirestore.getInstance()

    /**
     * Intializes the library checking in the user is already logged, in
     */
    fun checkAlreadyLoggedIn(
        userSettingsEntity: UserSettingsEntity, firebaseLoginUseCases: FirebaseLoginUseCase
    ) = MainScope().launch(Dispatchers.IO) {
        auth.let {
            userSettingsEntity.firebaseToken?.let { token ->
                firebaseLoginUseCases.signInWithGoogle(token)
            }
        }
    }

    suspend fun signInWithCredentials(credential: AuthCredential): Boolean {
        if (userKey != null) {
            return true
        }

        return suspendCoroutine {
            auth.signInWithCredential(credential).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    it.resume(true)
                } else {
                    it.resume(true)
                }
            }.addOnCanceledListener {
                it.resume(true)
            }
        }
    }
}

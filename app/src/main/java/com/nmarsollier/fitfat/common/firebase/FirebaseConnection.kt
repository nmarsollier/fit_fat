package com.nmarsollier.fitfat.common.firebase

import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FirebaseConnection internal constructor(
    private val googleAuthService: GoogleAuthService
) {
    private val mutableSharedFlow = MutableSharedFlow<Boolean>(replay = 0)
    val statusFlow: SharedFlow<Boolean> = mutableSharedFlow

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    val userKey: String?
        get() = auth.currentUser?.uid

    val instance
        get() = FirebaseFirestore.getInstance()

    /**
     * Intializes the library checking in the user is already logged, in
     */
    fun checkAlreadyLoggedIn(
        firebaseToken: String?
    ) = MainScope().launch(Dispatchers.IO) {
        auth.let {
            firebaseToken?.let { token ->
                signInWithGoogleToken(token)
            }
        }
    }

    suspend fun signInWithGoogleToken(googleToken: String): Boolean {
        val credential = GoogleAuthProvider.getCredential(googleToken, null)

        if (userKey != null) {
            MainScope().launch {
                mutableSharedFlow.emit(true)
            }
            return true
        }

        return suspendCoroutine {
            auth.signInWithCredential(credential).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    MainScope().launch {
                        mutableSharedFlow.emit(true)
                    }
                    it.resume(true)
                } else {
                    MainScope().launch {
                        mutableSharedFlow.emit(false)
                    }
                    it.resume(true)
                }
            }.addOnCanceledListener {
                MainScope().launch {
                    mutableSharedFlow.emit(false)
                }
                it.resume(true)
            }
        }
    }

    suspend fun signInWithGoogle(activity: ComponentActivity): GoogleAuthResult {
        val googleResult = googleAuthService.login(activity)
        return suspendCoroutine {
            when (googleResult) {
                is GoogleAuthResult.Error -> {
                    it.resume(googleResult)
                }

                is GoogleAuthResult.Success -> {
                    activity.lifecycleScope.launch {
                        signInWithGoogleToken(googleResult.token).let { fbResult ->
                            if (fbResult) {
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

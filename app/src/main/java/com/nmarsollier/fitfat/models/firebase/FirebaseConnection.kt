package com.nmarsollier.fitfat.models.firebase

import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
    ) = CoroutineScope(Dispatchers.IO).launch {
        auth.let {
            firebaseToken?.let { token ->
                signInWithGoogleToken(token)
            }
        }
    }

    suspend fun signInWithGoogleToken(googleToken: String): Boolean {
        val credential = GoogleAuthProvider.getCredential(googleToken, null)

        if (userKey != null) {
            CoroutineScope(Dispatchers.IO).launch {
                mutableSharedFlow.emit(true)
            }
            return true
        }

        return suspendCoroutine { continuation ->
            CoroutineScope(Dispatchers.IO).launch {
                auth.signInWithCredential(credential).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        CoroutineScope(Dispatchers.IO).launch {
                            mutableSharedFlow.emit(true)
                        }
                        continuation.resume(true)
                    } else {
                        CoroutineScope(Dispatchers.IO).launch {
                            mutableSharedFlow.emit(false)
                        }
                        continuation.resume(true)
                    }
                }.addOnCanceledListener {
                    CoroutineScope(Dispatchers.IO).launch {
                        mutableSharedFlow.emit(false)
                    }
                    continuation.resume(true)
                }
            }
        }
    }

    suspend fun signInWithGoogle(activity: ComponentActivity): GoogleAuthResult {
        return suspendCoroutine { continuation ->
            CoroutineScope(Dispatchers.IO).launch {
                when (val googleResult = googleAuthService.login(activity)) {
                    is GoogleAuthResult.Error -> {
                        continuation.resume(googleResult)
                    }

                    is GoogleAuthResult.Success -> {
                        activity.lifecycleScope.launch {
                            signInWithGoogleToken(googleResult.token).let { fbResult ->
                                if (fbResult) {
                                    continuation.resume(googleResult)
                                } else {
                                    continuation.resume(GoogleAuthResult.Error)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

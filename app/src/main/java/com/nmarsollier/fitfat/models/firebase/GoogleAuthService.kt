package com.nmarsollier.fitfat.models.firebase

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.nmarsollier.fitfat.BuildConfig
import com.nmarsollier.fitfat.utils.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

sealed class GoogleAuthResult {
    data class Success(val token: String) : GoogleAuthResult()
    data object Error : GoogleAuthResult()
}

class GoogleAuthService internal constructor(
    private val logger: Logger
) {
    private fun getLoginIntent(activity: ComponentActivity): Intent {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(BuildConfig.WEB_CLIENT_ID).build()

        return GoogleSignIn.getClient(
            activity, gso
        ).signInIntent
    }

    private var registration: ActivityResultLauncher<Intent>? = null
    private var loginCallback: ((result: ActivityResult?) -> Unit)? = null

    fun registerForLogin(activity: ComponentActivity) {
        registration = activity.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
        ) { data ->
            loginCallback?.invoke(data)
        }
    }

    suspend fun login(activity: ComponentActivity): GoogleAuthResult =
        suspendCoroutine { continuation ->
            CoroutineScope(Dispatchers.IO).launch {
                val intent = getLoginIntent(activity)

                loginCallback = { result ->
                    val task = GoogleSignIn.getSignedInAccountFromIntent(result!!.data)
                    try {
                        task.getResult(ApiException::class.java)?.idToken?.let { token ->
                            continuation.resume(GoogleAuthResult.Success(token))
                        }
                    } catch (e: ApiException) {
                        continuation.resume(GoogleAuthResult.Error)
                        logger.e("Google sign in failed", e)
                    }
                }

                registration?.launch(intent)
            }
        }
}

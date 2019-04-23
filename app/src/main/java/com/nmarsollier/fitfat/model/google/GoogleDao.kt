package com.nmarsollier.fitfat.model.google

import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.nmarsollier.fitfat.R
import com.nmarsollier.fitfat.utils.logger
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch

sealed class GoogleLoginResult {
    data class Success(val token: String) : GoogleLoginResult()
    data class Error(val e: Exception) : GoogleLoginResult()
}

object GoogleDao {
    private val logger by logger()

    private var registration: ActivityResultLauncher<Intent>? = null

    private var loginCallback: ((result: ActivityResult?) -> Unit)? = null

    fun registerForLogin(fragment: Fragment) {
        registration = fragment.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
        ) { data ->
            loginCallback?.invoke(data)
        }
    }

    fun login(fragment: Fragment): Flow<GoogleLoginResult> = channelFlow {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(fragment.getString(R.string.web_client_id))
            .build()

        val intent = GoogleSignIn.getClient(
            fragment.requireActivity(),
            gso
        ).signInIntent

        loginCallback = { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result!!.data)
            try {
                task.getResult(ApiException::class.java)?.idToken?.let { token ->
                    launch {
                        send(GoogleLoginResult.Success(token))
                    }
                }
            } catch (e: ApiException) {
                launch {
                    send(GoogleLoginResult.Error(e))
                }
                logger.severe("Google sign in failed $e")
            }
        }

        registration?.launch(intent)
        awaitClose()
    }
}

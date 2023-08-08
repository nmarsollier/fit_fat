package com.nmarsollier.fitfat.useCases

import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.nmarsollier.fitfat.model.google.GoogleDao
import com.nmarsollier.fitfat.utils.logger
import kotlinx.coroutines.coroutineScope
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

sealed class GoogleLoginResult {
    data class Success(val token: String) : GoogleLoginResult()
    data class Error(val e: Exception) : GoogleLoginResult()
}

class GoogleUseCase {
    private var registration: ActivityResultLauncher<Intent>? = null
    private var loginCallback: ((result: ActivityResult?) -> Unit)? = null
    fun registerForLogin(fragment: Fragment) {
        registration = fragment.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
        ) { data ->
            loginCallback?.invoke(data)
        }
    }

    suspend fun login(fragment: Fragment): GoogleLoginResult = coroutineScope {
        suspendCoroutine {
            val intent = GoogleDao.getLoginIntent(fragment)

            loginCallback = { result ->
                val task = GoogleSignIn.getSignedInAccountFromIntent(result!!.data)
                try {
                    task.getResult(ApiException::class.java)?.idToken?.let { token ->
                        it.resume(GoogleLoginResult.Success(token))
                    }
                } catch (e: ApiException) {
                    it.resume(GoogleLoginResult.Error(e))
                    logger.severe("Google sign in failed $e")
                }
            }

            registration?.launch(intent)
        }
    }
}
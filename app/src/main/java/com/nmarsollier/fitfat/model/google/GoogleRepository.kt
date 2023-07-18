package com.nmarsollier.fitfat.model.google

import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.nmarsollier.fitfat.model.firebase.FirebaseRepository
import com.nmarsollier.fitfat.model.userSettings.UserSettingsRepository
import com.nmarsollier.fitfat.ui.options.OptionsFragment
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch

object GoogleRepository {
    fun login(fragment: OptionsFragment): Flow<GoogleLoginResult> = channelFlow {
        GoogleDao.login(fragment).collect { result ->
            when (result) {
                is GoogleLoginResult.Error -> {
                    send(result)
                    close()
                }
                is GoogleLoginResult.Success -> {
                    val context = fragment.requireContext()
                    UserSettingsRepository.updateFirebaseToken(context, result.token)

                    fragment.lifecycleScope.launch {
                        FirebaseRepository.googleAuth(result.token).collect { fbResult ->
                            if (fbResult == true) {
                                FirebaseRepository.downloadUserSettings(context, result.token)
                                FirebaseRepository.downloadMeasurements(context)
                                send(result)
                                close()
                            } else {
                                send(GoogleLoginResult.Error(Exception("FirebaseRepository.googleAuth error")))
                                close()
                            }
                        }
                    }
                }
            }
        }
        awaitClose()
    }

    fun registerForLogin(fragment: Fragment) =
        GoogleDao.registerForLogin(fragment)
}

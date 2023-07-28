package com.nmarsollier.fitfat.model.google

import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.nmarsollier.fitfat.model.firebase.FirebaseRepository
import com.nmarsollier.fitfat.model.userSettings.UserSettingsRepository
import com.nmarsollier.fitfat.ui.options.OptionsFragment
import com.nmarsollier.fitfat.utils.collectOnce
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch

object GoogleRepository {
    fun login(fragment: OptionsFragment): Flow<GoogleLoginResult> = channelFlow {
        GoogleDao.login(fragment).collectOnce { result ->
            when (result) {
                is GoogleLoginResult.Error -> {
                    send(result)
                    UserSettingsRepository.updateFirebaseToken(null)
                }

                is GoogleLoginResult.Success -> {
                    fragment.lifecycleScope.launch {
                        FirebaseRepository.googleAuth(result.token).collect { fbResult ->
                            if (fbResult == true) {
                                UserSettingsRepository.updateFirebaseToken(result.token)
                                FirebaseRepository.downloadUserSettings()
                                FirebaseRepository.downloadMeasurements()
                                send(result)
                            } else {
                                send(GoogleLoginResult.Error(Exception("FirebaseRepository.googleAuth error")))
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

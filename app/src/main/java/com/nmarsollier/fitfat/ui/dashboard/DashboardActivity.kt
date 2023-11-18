package com.nmarsollier.fitfat.ui.dashboard

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.MaterialTheme
import com.nmarsollier.fitfat.firebase.FirebaseConnection
import com.nmarsollier.fitfat.firebase.GoogleAuthService
import com.nmarsollier.fitfat.userSettings.model.UserSettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class DashboardActivity : AppCompatActivity() {
    private val googleAuthService: GoogleAuthService by inject()
    private val firebaseConnection: FirebaseConnection by inject()
    private val userSettingsRepository: UserSettingsRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        googleAuthService.registerForLogin(this)
        MainScope().launch(Dispatchers.IO) {
            firebaseConnection.checkAlreadyLoggedIn(
                userSettingsRepository.findCurrent().value.firebaseToken
            )
        }

        setContent {
            MaterialTheme {
                DashboardScreen()
            }
        }
    }
}

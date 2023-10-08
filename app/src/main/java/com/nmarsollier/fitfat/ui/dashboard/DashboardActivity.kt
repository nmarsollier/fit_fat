package com.nmarsollier.fitfat.ui.dashboard

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.MaterialTheme
import com.nmarsollier.fitfat.model.firebase.FirebaseConnection
import com.nmarsollier.fitfat.model.firebase.GoogleAuth
import com.nmarsollier.fitfat.model.userSettings.UserSettingsRepository
import com.nmarsollier.fitfat.useCases.FirebaseLoginUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class DashboardActivity : AppCompatActivity() {
    private val googleAuth: GoogleAuth by inject()
    private val firebaseConnection: FirebaseConnection by inject()
    private val firebaseUseCase: FirebaseLoginUseCase by inject()
    private val userSettingsRepository: UserSettingsRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        googleAuth.registerForLogin(this)
        MainScope().launch(Dispatchers.IO) {
            firebaseConnection.checkAlreadyLoggedIn(
                userSettingsRepository.load(), firebaseUseCase
            )
        }

        setContent {
            MaterialTheme {
                DashboardScreen()
            }
        }
    }
}

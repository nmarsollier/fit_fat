package com.nmarsollier.fitfat

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.rememberNavController
import com.nmarsollier.fitfat.common.firebase.FirebaseConnection
import com.nmarsollier.fitfat.common.firebase.GoogleAuthService
import com.nmarsollier.fitfat.common.navigation.AppNavActions
import com.nmarsollier.fitfat.common.navigation.AppNavigationHost
import com.nmarsollier.fitfat.common.navigation.NavigationProvider
import com.nmarsollier.fitfat.userSettings.model.UserSettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.compose.koinInject

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
                AppContent()
            }
        }
    }
}

@Composable
fun AppContent(
    appNavActionProvider: NavigationProvider = koinInject(),
) {
    // navigation
    val navController = rememberNavController()
    remember(navController) {
        AppNavActions(navController).also {
            appNavActionProvider.appNavActions = it
        }
    }

    AppNavigationHost(
        AppNavActions(navController),
    )
}

package com.nmarsollier.fitfat

import android.os.*
import androidx.activity.compose.*
import androidx.appcompat.app.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.compose.*
import com.nmarsollier.fitfat.models.firebase.*
import com.nmarsollier.fitfat.models.userSettings.*
import com.nmarsollier.fitfat.ui.common.navigation.*
import com.nmarsollier.fitfat.ui.common.theme.*
import kotlinx.coroutines.*
import org.koin.android.ext.android.*
import org.koin.compose.*

class AppActivity : AppCompatActivity() {
    private val googleAuthService: GoogleAuthService by inject()
    private val firebaseConnection: FirebaseConnection by inject()
    private val userSettingsRepository: UserSettingsRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        googleAuthService.registerForLogin(this)
        CoroutineScope(Dispatchers.IO).launch {
            firebaseConnection.checkAlreadyLoggedIn(
                userSettingsRepository.findCurrent().firebaseToken
            )
        }

        setContent {
            AppContent()
        }
    }
}

@Composable
fun AppContent(
    appNavActionProvider: NavigationProvider = koinInject(),
) {
    val navController = rememberNavController()
    remember(navController) {
        AppNavActions(navController).also {
            appNavActionProvider.appNavActions = it
        }
    }

    MaterialTheme(
        colorScheme = LightColorScheme
    ) {
        AppNavigationHost()
    }
}

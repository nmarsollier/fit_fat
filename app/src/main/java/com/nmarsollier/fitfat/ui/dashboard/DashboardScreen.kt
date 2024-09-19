package com.nmarsollier.fitfat.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.lifecycle.*
import com.nmarsollier.fitfat.ui.measures.list.*
import com.nmarsollier.fitfat.ui.stats.*
import com.nmarsollier.fitfat.ui.userSettings.*
import org.koin.androidx.compose.*

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState(viewModel.viewModelScope.coroutineContext)

    DashboardContent(state, viewModel::reduce)
}

@Composable
fun DashboardContent(
    screen: Screen,
    reduce: (Screen) -> Unit,
) {
    Scaffold(bottomBar = { DashboardNavigationMenu(screen, reduce) } // Bottom navigation bar
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(it)
        ) {
            when (screen) {
                Screen.OPTIONS -> OptionsScreen()

                Screen.MEASURES_LIST -> MeasuresListScreen()

                Screen.STATS -> StatsScreen()
            }
        }
    }
}


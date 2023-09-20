package com.nmarsollier.fitfat.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewModelScope
import com.nmarsollier.fitfat.measures.ui.measuresList.MeasuresListScreen
import com.nmarsollier.fitfat.stats.ui.stats.StatsScreen
import com.nmarsollier.fitfat.ui.preview.Samples
import com.nmarsollier.fitfat.userSettings.ui.options.OptionsScreen
import com.nmarsollier.fitfat.utils.ui.preview.KoinPreview
import com.nmarsollier.fitfat.utils.ui.theme.AppColors
import org.koin.androidx.compose.koinViewModel

@Composable
fun DashboardScreen(viewModel: DashboardViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsState(viewModel.viewModelScope.coroutineContext)
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    viewModel.init()
                }

                else -> Unit
            }
        }.also {
            lifecycleOwner.lifecycle.addObserver(it)
        }

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    DashboardContent(state, viewModel)
}

@Composable
fun DashboardContent(state: DashboardState, reducer: DashboardReducer) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(AppColors.background)
    ) {
        Row(
            modifier = Modifier
                .weight(1f, true)
                .fillMaxWidth()
        ) {
            when (state.selectedTab) {
                Screen.OPTIONS -> OptionsScreen()

                Screen.MEASURES_LIST -> MeasuresListScreen()

                Screen.STATS -> StatsScreen()
            }
        }
        Row(
            verticalAlignment = Alignment.Bottom
        ) {
            DashboardNavigationMenu(state, reducer)
        }
    }
}

@Preview
@Composable
fun DashboardContentPreview() {
    KoinPreview {
        DashboardContent(
            DashboardState.Ready(
                tab = Screen.MEASURES_LIST
            ), DashboardViewModel.Samples.reducer()
        )
    }
}

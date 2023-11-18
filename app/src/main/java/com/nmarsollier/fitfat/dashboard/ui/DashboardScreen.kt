package com.nmarsollier.fitfat.dashboard.ui

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
import com.nmarsollier.fitfat.common.ui.preview.KoinPreview
import com.nmarsollier.fitfat.common.ui.theme.AppColors
import com.nmarsollier.fitfat.common.ui.viewModel.Reducer
import com.nmarsollier.fitfat.dashboard.samples.Samples
import com.nmarsollier.fitfat.measures.ui.list.MeasuresListScreen
import com.nmarsollier.fitfat.stats.ui.StatsScreen
import com.nmarsollier.fitfat.userSettings.ui.OptionsScreen
import org.koin.androidx.compose.koinViewModel

@Composable
fun DashboardScreen(viewModel: DashboardView = koinViewModel()) {
    val state by viewModel.state.collectAsState(viewModel.viewModelScope.coroutineContext)
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    viewModel.reduce(DashboardEvent.Initialize)
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
fun DashboardContent(state: DashboardState, reducer: Reducer<DashboardEvent>) {
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
            ), DashboardView.Samples.reducer()
        )
    }
}

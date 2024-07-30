package com.nmarsollier.fitfat.dashboard.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewModelScope
import com.nmarsollier.fitfat.common.ui.preview.KoinPreview
import com.nmarsollier.fitfat.dashboard.samples.Samples
import com.nmarsollier.fitfat.measures.ui.list.MeasuresListScreen
import com.nmarsollier.fitfat.stats.ui.StatsScreen
import com.nmarsollier.fitfat.userSettings.ui.OptionsScreen
import org.koin.androidx.compose.koinViewModel

@Composable
fun DashboardScreen(viewModel: DashboardViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsState(viewModel.viewModelScope.coroutineContext)

    LaunchedEffect(viewModel) {
        viewModel.reduce(DashboardAction.Initialize)
    }

    DashboardContent(state, viewModel::reduce)
}

@Composable
fun DashboardContent(state: DashboardState, reduce: (DashboardAction) -> Unit) {
    Column(
        Modifier.fillMaxWidth()
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
            DashboardNavigationMenu(state, reduce)
        }
    }
}

@Preview
@Composable
private fun DashboardContentPreview() {
    KoinPreview {
        DashboardContent(
            DashboardState.Ready(
                tab = Screen.MEASURES_LIST
            ), DashboardViewModel.Samples::reduce
        )
    }
}

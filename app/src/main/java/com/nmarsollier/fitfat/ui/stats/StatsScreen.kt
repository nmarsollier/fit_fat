package com.nmarsollier.fitfat.ui.stats

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.tooling.preview.*
import androidx.lifecycle.*
import com.nmarsollier.fitfat.models.measures.*
import com.nmarsollier.fitfat.models.measures.db.*
import com.nmarsollier.fitfat.models.userSettings.*
import com.nmarsollier.fitfat.ui.common.preview.*
import com.nmarsollier.fitfat.ui.common.viewModel.reduceWith
import com.nmarsollier.fitfat.ui.common.views.*
import com.nmarsollier.fitfat.ui.measures.*
import com.nmarsollier.fitfat.ui.measures.dialog.*
import com.nmarsollier.fitfat.ui.userSettings.*
import org.koin.androidx.compose.*

@Composable
fun StatsScreen(
    viewModel: StatsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState(viewModel.viewModelScope.coroutineContext)

    StatsContent(state, viewModel::reduce)
}

@Composable
fun StatsContent(state: StatsState, reducer: (StatsAction) -> Unit) {
    Scaffold(topBar = {
        StatsMenu()
    }) {
        Column(
            modifier = Modifier.padding(it),
        ) {
            when (state) {
                is StatsState.Loading -> LoadingView()
                is StatsState.Ready -> {
                    StatsContentDetail(state, reducer)

                    if (state.showMethod) {
                        MeasureMethodDialog(state.selectedMethod) {
                            if (it != null) {
                                StatsAction.UpdateMethod(it)
                            } else {
                                StatsAction.ToggleShowMethod
                            }.reduceWith(reducer)
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun StatsScreenPreview() {
    KoinPreview {
        StatsContent(
            StatsState.Ready(
                MeasureMethod.WEIGHT_ONLY,
                UserSettings.Samples.simpleData,
                Measure.Samples.simpleData,
                showMethod = false
            )
        ) {}
    }
}

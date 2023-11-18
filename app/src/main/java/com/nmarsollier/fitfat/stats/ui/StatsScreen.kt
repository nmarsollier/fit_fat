package com.nmarsollier.fitfat.stats.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewModelScope
import com.nmarsollier.fitfat.common.ui.viewModel.Reducer
import com.nmarsollier.fitfat.measures.model.Measure
import com.nmarsollier.fitfat.measures.model.db.MeasureMethod
import com.nmarsollier.fitfat.measures.ui.dialog.MeasureMethodDialog
import com.nmarsollier.fitfat.measures.samples.Samples
import com.nmarsollier.fitfat.stats.samples.Samples
import com.nmarsollier.fitfat.userSettings.model.UserSettings
import com.nmarsollier.fitfat.userSettings.samples.Samples
import org.koin.androidx.compose.koinViewModel

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun StatsScreen(viewModel: StatsView = koinViewModel()) {
    val lifecycleOwner = LocalLifecycleOwner.current

    val state by viewModel.state.collectAsState(viewModel.viewModelScope.coroutineContext)

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    viewModel.reduce(StatsEvent.Initialize)
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

    StatsContent(state, viewModel)
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun StatsContent(state: StatsState, reducer: Reducer<StatsEvent>) {
    Scaffold(topBar = {
        StatsMenu()
    }) {
        Column {
            when (state) {
                is StatsState.Loading -> com.nmarsollier.fitfat.common.ui.views.LoadingView()
                is StatsState.Ready -> {
                    StatsContentDetail(state, reducer)

                    if (state.showMethod) {
                        MeasureMethodDialog(state.selectedMethod) {
                            if (it != null) {
                                reducer.reduce(StatsEvent.UpdateMethod(it))
                            } else {
                                reducer.reduce(StatsEvent.ToggleShowMethod)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun StatsScreenPreview() {
    com.nmarsollier.fitfat.common.ui.preview.KoinPreview {
        StatsContent(
            StatsState.Ready(
                MeasureMethod.WEIGHT_ONLY,
                UserSettings.Samples.simpleData.value,
                Measure.Samples.simpleData.map { it.value },
                showMethod = false
            ), StatsView.Samples.reducer()
        )
    }
}

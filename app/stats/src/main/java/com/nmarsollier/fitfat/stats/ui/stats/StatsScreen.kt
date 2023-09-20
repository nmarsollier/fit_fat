package com.nmarsollier.fitfat.stats.ui.stats

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
import com.nmarsollier.fitfat.measures.model.Measure
import com.nmarsollier.fitfat.measures.model.db.MeasureMethod
import com.nmarsollier.fitfat.measures.ui.dialogs.MeasureMethodDialog
import com.nmarsollier.fitfat.measures.ui.utils.preview.Samples
import com.nmarsollier.fitfat.stats.ui.utils.preview.Samples
import com.nmarsollier.fitfat.userSettings.model.UserSettings
import com.nmarsollier.fitfat.userSettings.ui.utils.preview.Samples
import com.nmarsollier.fitfat.utils.ui.preview.KoinPreview
import com.nmarsollier.fitfat.utils.ui.views.LoadingView
import org.koin.androidx.compose.koinViewModel

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun StatsScreen(viewModel: StatsViewModel = koinViewModel()) {
    val lifecycleOwner = LocalLifecycleOwner.current

    val state by viewModel.state.collectAsState(viewModel.viewModelScope.coroutineContext)

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

    StatsContent(state, viewModel)
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun StatsContent(state: StatsState, reducer: StatsReducer) {
    Scaffold(topBar = {
        StatsMenu()
    }) {
        Column {
            when (state) {
                is StatsState.Loading -> LoadingView()
                is StatsState.Ready -> {
                    StatsContentDetail(state, reducer)

                    if (state.showMethod) {
                        MeasureMethodDialog(state.selectedMethod) {
                            if (it != null) {
                                reducer.updateMethod(it)
                            } else {
                                reducer.toggleShowMethod()
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
    KoinPreview {
        StatsContent(
            StatsState.Ready(
                MeasureMethod.WEIGHT_ONLY,
                UserSettings.Samples.simpleData.value,
                Measure.Samples.simpleData.map { it.value },
                showMethod = false
            ), StatsViewModel.Samples.reducer()
        )
    }
}

package com.nmarsollier.fitfat.ui.stats

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
import com.nmarsollier.fitfat.model.measures.Measure
import com.nmarsollier.fitfat.model.measures.MeasureMethod
import com.nmarsollier.fitfat.model.userSettings.UserSettingsEntity
import com.nmarsollier.fitfat.ui.common.LoadingView
import com.nmarsollier.fitfat.ui.common.MeasureMethodDialog
import com.nmarsollier.fitfat.utils.KoinPreview
import com.nmarsollier.fitfat.utils.Samples
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
            when (val st = state) {
                is StatsState.Loading -> LoadingView()
                is StatsState.Ready -> {
                    StatsContentDetail(st, reducer)

                    if (st.showMethod) {
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
                UserSettingsEntity.Samples.simpleData,
                Measure.Samples.simpleData,
                showMethod = false
            ), StatsViewModel.Samples.reducer()
        )
    }
}

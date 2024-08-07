package com.nmarsollier.fitfat.userSettings.ui

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewModelScope
import com.nmarsollier.fitfat.R
import com.nmarsollier.fitfat.common.ui.preview.KoinPreview
import com.nmarsollier.fitfat.common.ui.views.LoadingView
import com.nmarsollier.fitfat.userSettings.model.UserSettings
import com.nmarsollier.fitfat.userSettings.samples.Samples
import org.koin.androidx.compose.koinViewModel

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun OptionsScreen(viewModel: OptionsViewModel = koinViewModel()) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val state by viewModel.state.collectAsState(viewModel.viewModelScope.coroutineContext)

    LaunchedEffect(viewModel) {
        viewModel.reduce(OptionsAction.Initialize)
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    viewModel.reduce(OptionsAction.SaveSettings)
                }

                else -> Unit
            }
        }.also {
            lifecycleOwner.lifecycle.addObserver(it)
        }

        onDispose {
            viewModel.reduce(OptionsAction.SaveSettings)
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    OptionsContent(state, viewModel::reduce)
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun OptionsContent(
    state: OptionsState,
    reduce: (OptionsAction) -> Unit
) {
    Scaffold(
        topBar = { OptionsMenu(reduce) }
    ) {
        Column {
            when (state) {
                OptionsState.GoogleLoginError -> {
                    Toast.makeText(
                        LocalContext.current,
                        stringResource(id = R.string.google_error),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                OptionsState.Loading -> LoadingView()
                is OptionsState.Ready -> OptionsContentDetail(
                    state = state, reduce
                )
            }
        }
    }
}

@Preview
@Composable
private fun OptionsScreenPreview() {
    KoinPreview {
        OptionsContent(
            OptionsState.Ready(
                UserSettings.Samples.simpleData.value, false
            ),
            OptionsViewModel.Samples::reduce
        )
    }
}

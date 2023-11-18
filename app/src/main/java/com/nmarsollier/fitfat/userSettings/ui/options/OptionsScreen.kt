package com.nmarsollier.fitfat.userSettings.ui.options

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewModelScope
import com.nmarsollier.fitfat.R
import com.nmarsollier.fitfat.userSettings.model.UserSettings
import com.nmarsollier.fitfat.userSettings.ui.utils.preview.Samples
import com.nmarsollier.fitfat.utils.ui.preview.KoinPreview
import com.nmarsollier.fitfat.utils.ui.views.LoadingView
import org.koin.androidx.compose.koinViewModel

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun OptionsScreen(viewModel: OptionsViewModel = koinViewModel()) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val state by viewModel.state.collectAsState(viewModel.viewModelScope.coroutineContext)

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    viewModel.load()
                }

                Lifecycle.Event.ON_STOP -> {
                    viewModel.saveSettings()
                }

                else -> Unit
            }
        }.also {
            lifecycleOwner.lifecycle.addObserver(it)
        }

        onDispose {
            viewModel.saveSettings()
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    OptionsContent(state, viewModel)
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun OptionsContent(
    state: OptionsState,
    reducer: OptionsReducer
) {
    Scaffold(
        topBar = { OptionsMenu(reducer) }
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

                OptionsState.Loading -> com.nmarsollier.fitfat.utils.ui.views.LoadingView()
                is OptionsState.Ready -> OptionsContentDetail(
                    state = state, reducer
                )
            }
        }
    }
}

@Preview
@Composable
fun OptionsScreenPreview() {
    com.nmarsollier.fitfat.utils.ui.preview.KoinPreview {
        OptionsContent(
            OptionsState.Ready(
                UserSettings.Samples.simpleData.value, false
            ),
            OptionsViewModel.Samples.reducer()
        )
    }
}

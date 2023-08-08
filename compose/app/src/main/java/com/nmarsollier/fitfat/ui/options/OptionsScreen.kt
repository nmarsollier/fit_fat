package com.nmarsollier.fitfat.ui.options

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import com.nmarsollier.fitfat.model.userSettings.UserSettingsEntity
import com.nmarsollier.fitfat.ui.common.LoadingView
import com.nmarsollier.fitfat.utils.KoinPreview
import com.nmarsollier.fitfat.utils.Samples
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
    Scaffold(topBar = { OptionsMenu(reducer) }) {
        Column {
            when (val st = state) {
                OptionsState.GoogleLoginError -> {
                    Toast.makeText(
                        LocalContext.current,
                        stringResource(id = R.string.google_error),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                OptionsState.Loading -> LoadingView()
                is OptionsState.Ready -> OptionsContentDetail(
                    state = st, reducer
                )
            }
        }
    }
}

@Preview
@Composable
fun OptionsScreenPreview() {
    KoinPreview {
        OptionsContent(
            OptionsState.Ready(
                UserSettingsEntity.Samples.simpleData, false
            ),
            OptionsViewModel.Samples.reducer()
        )
    }
}

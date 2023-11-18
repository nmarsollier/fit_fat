package com.nmarsollier.fitfat.measures.ui.edit

import android.annotation.SuppressLint
import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewModelScope
import com.nmarsollier.fitfat.R
import com.nmarsollier.fitfat.measures.model.Measure
import com.nmarsollier.fitfat.measures.model.db.MeasureData
import com.nmarsollier.fitfat.measures.ui.dialog.MeasureMethodDialog
import com.nmarsollier.fitfat.measures.samples.Samples
import com.nmarsollier.fitfat.userSettings.model.UserSettings
import com.nmarsollier.fitfat.userSettings.samples.Samples
import com.nmarsollier.fitfat.common.ui.dialogs.HelpDialog
import com.nmarsollier.fitfat.common.ui.preview.KoinPreview
import com.nmarsollier.fitfat.common.ui.viewModel.Reducer
import com.nmarsollier.fitfat.common.ui.views.LoadingView
import org.koin.androidx.compose.koinViewModel

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun EditMeasureScreen(
    initialMeasure: MeasureData? = null,
    viewModel: EditMeasureView = koinViewModel()
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val state by viewModel.state.collectAsState(viewModel.viewModelScope.coroutineContext)

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    viewModel.reduce(EditMeasureEvent.Initialize(initialMeasure))
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

    EditMeasureContent(state, viewModel)
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun EditMeasureContent(
    state: EditMeasureState, reducer: Reducer<EditMeasureEvent>
) {
    val context = LocalContext.current as? Activity

    Scaffold(topBar = {
        EditMeasureMenu(state, reducer)
    }) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            when (state) {
                is EditMeasureState.Ready -> {
                    EditMeasureDetails(
                        state.userSettings, state.measure, reducer
                    )

                    if (state.showHelp != null) {
                        HelpDialog(helpRes = state.showHelp) {
                            reducer.reduce(EditMeasureEvent.ToggleHelp(null))
                        }
                    }

                    if (state.showMethod) {
                        MeasureMethodDialog(state.measure.measureMethod) {
                            if (it != null) {
                                reducer.reduce(EditMeasureEvent.UpdateMeasureMethod(it))
                            } else {
                                reducer.reduce(EditMeasureEvent.ToggleShowMethod)
                            }
                        }
                    }
                }

                is EditMeasureState.Invalid -> {
                    Toast.makeText(
                        context, R.string.new_measure_error, Toast.LENGTH_LONG
                    ).show()
                }

                EditMeasureState.Close -> context?.finish()
                is EditMeasureState.Loading -> LoadingView()
            }
        }
    }
}

@Preview
@Composable
fun EditMeasureContentPreview() {
    KoinPreview {
        EditMeasureContent(
            EditMeasureState.Ready(
                userSettings = UserSettings.Samples.simpleData.value,
                measure = Measure.Samples.simpleData[0].value,
                showHelp = null,
                showMethod = false,
                readOnly = false
            ),
            EditMeasureView.Samples.reducer()
        )
    }
}

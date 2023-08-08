package com.nmarsollier.fitfat.ui.editMeasure

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
import com.nmarsollier.fitfat.model.measures.Measure
import com.nmarsollier.fitfat.model.userSettings.UserSettingsEntity
import com.nmarsollier.fitfat.ui.common.HelpDialog
import com.nmarsollier.fitfat.ui.common.LoadingView
import com.nmarsollier.fitfat.ui.common.MeasureMethodDialog
import com.nmarsollier.fitfat.utils.KoinPreview
import com.nmarsollier.fitfat.utils.Samples
import org.koin.androidx.compose.koinViewModel

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun EditMeasureScreen(
    initialMeasure: Measure? = null, viewModel: EditMeasureViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState(viewModel.viewModelScope.coroutineContext)

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    viewModel.init(initialMeasure)
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
    state: EditMeasureState, reducer: EditMeasureReducer
) {
    val context = LocalContext.current as? Activity

    Scaffold(topBar = {
        EditMeasureMenu(state, reducer)
    }) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            when (val st = state) {
                is EditMeasureState.Ready -> {
                    EditMeasureDetails(
                        st.userSettingsEntity, st.measure, reducer
                    )

                    if (st.showHelp != null) {
                        HelpDialog(helpRes = st.showHelp) {
                            reducer.toggleHelp(null)
                        }
                    }

                    if (st.showMethod) {
                        MeasureMethodDialog(st.measure.measureMethod) {
                            if (it != null) {
                                reducer.updateMeasureMethod(it)
                            } else {
                                reducer.toggleShowMethod()
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
                userSettingsEntity = UserSettingsEntity.Samples.simpleData,
                measure = Measure.Samples.simpleData[0],
                showHelp = null,
                showMethod = false,
                readOnly = false
            ),
            EditMeasureViewModel.Samples.reducer()
        )
    }
}

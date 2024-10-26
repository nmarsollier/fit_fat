package com.nmarsollier.fitfat.ui.measures.edit

import android.widget.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.tooling.preview.*
import androidx.lifecycle.*
import com.nmarsollier.fitfat.R
import com.nmarsollier.fitfat.models.measures.*
import com.nmarsollier.fitfat.models.userSettings.*
import com.nmarsollier.fitfat.ui.common.dialogs.*
import com.nmarsollier.fitfat.ui.common.navigation.*
import com.nmarsollier.fitfat.ui.common.preview.*
import com.nmarsollier.fitfat.ui.common.viewModel.reduceWith
import com.nmarsollier.fitfat.ui.common.views.*
import com.nmarsollier.fitfat.ui.measures.*
import com.nmarsollier.fitfat.ui.measures.dialog.*
import com.nmarsollier.fitfat.ui.userSettings.*
import org.koin.androidx.compose.*
import org.koin.compose.*

@Composable
fun EditMeasureScreen(
    initialMeasure: Measure? = null,
    viewModel: EditMeasureViewModel = koinViewModel(),
    navActions: AppNavActions = koinInject()
) {
    val state by viewModel.state.collectAsState(viewModel.viewModelScope.coroutineContext)
    val event by viewModel.event.collectAsState(null)

    DisposableEffect(initialMeasure) {
        EditMeasureAction.Initialize(initialMeasure).reduceWith(viewModel::reduce)
        onDispose { }
    }

    when (event) {
        EditMeasureEvent.Close -> {
            navActions.navigateUp()
        }

        EditMeasureEvent.Invalid -> {
            Toast.makeText(
                LocalContext.current, R.string.new_measure_error, Toast.LENGTH_LONG
            ).show()
        }

        else -> Unit
    }

    EditMeasureContent(state, viewModel::reduce)
}

@Composable
fun EditMeasureContent(
    state: EditMeasureState, reducer: (EditMeasureAction) -> Unit,
) {
    Scaffold(topBar = {
        EditMeasureMenu(state, reducer)
    }) {
        Surface(modifier = Modifier.padding(it)) {
            when (state) {
                is EditMeasureState.Ready -> {
                    EditMeasureDetails(
                        state.userSettings, state.measure, reducer
                    )

                    if (state.showHelp != null) {
                        HelpDialog(helpRes = state.showHelp) {
                            EditMeasureAction.ToggleHelp(null).reduceWith(reducer)
                        }
                    }

                    if (state.showMeasureMethod) {
                        MeasureMethodDialog(state.measure.measureMethod) {
                            if (it != null) {
                                EditMeasureAction.UpdateMeasureMethod(it).reduceWith(reducer)
                            } else {
                                EditMeasureAction.ToggleMeasureMethod.reduceWith(reducer)
                            }
                        }
                    }
                }

                is EditMeasureState.Loading -> {
                    LoadingView()
                }
            }
        }
    }
}

@Preview
@Composable
private fun EditMeasureContentPreview() {
    KoinPreview {
        EditMeasureContent(
            EditMeasureState.Ready(
                userSettings = UserSettings.Samples.simpleData,
                measure = Measure.Samples.simpleData[0],
                showHelp = null,
                showMeasureMethod = false,
                readOnly = false,
                isSaveEnabled = false
            )
        ) {}
    }
}

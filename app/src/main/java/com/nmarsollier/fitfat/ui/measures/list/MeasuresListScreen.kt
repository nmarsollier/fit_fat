package com.nmarsollier.fitfat.ui.measures.list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import androidx.lifecycle.*
import com.nmarsollier.fitfat.models.measures.*
import com.nmarsollier.fitfat.models.userSettings.*
import com.nmarsollier.fitfat.ui.common.navigation.*
import com.nmarsollier.fitfat.ui.common.preview.*
import com.nmarsollier.fitfat.ui.common.views.*
import com.nmarsollier.fitfat.ui.measures.*
import com.nmarsollier.fitfat.ui.userSettings.*
import org.koin.androidx.compose.*
import org.koin.compose.*

@Composable
fun MeasuresListScreen(
    viewModel: MeasuresListViewModel = koinViewModel(),
    navActions: AppNavActions = koinInject()
) {
    val state by viewModel.state.collectAsState(viewModel.viewModelScope.coroutineContext)
    val event by viewModel.event.collectAsState(null)

    when (val e = event) {
        is MeasuresListEvent.Redirect -> {
            when (val dst = e.destination) {
                is Destination.NewMeasure -> navActions.navigateNewMeasure()

                is Destination.ViewMeasure -> navActions.navigateEditMeasure(
                    dst.measure
                )
            }
        }

        else -> Unit
    }

    MeasuresListContent(state, viewModel::reduce)
}

@Composable
fun MeasuresListContent(
    state: MeasuresListState, reduce: (MeasuresListAction) -> Unit,
) {
    Scaffold(topBar = {
        MeasuresListMenu()
    }, floatingActionButton = {
        FloatingActionButton(
            onClick = {
                reduce(MeasuresListAction.OpenNewMeasure)
            },
            shape = CircleShape,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                "",
            )
        }
    }, floatingActionButtonPosition = FabPosition.End
    ) {
        Surface(
            modifier = Modifier.padding(it),
        ) {
            when (state) {
                MeasuresListState.Loading -> LoadingView()

                is MeasuresListState.Ready -> {
                    val userSettings by rememberUpdatedState(state.userSettings)

                    LazyColumn(Modifier.fillMaxSize()) {
                        items(state.measures, key = { it.uid }) { measure ->
                            SwipeToDelete(measure, userSettings, reduce)
                            HorizontalDivider(thickness = 1.dp, color = Color.LightGray)
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun MeasuresListScreenPreview() {
    KoinPreview {
        MeasuresListContent(
            MeasuresListState.Ready(
                UserSettings.Samples.simpleData, Measure.Samples.simpleData
            ),
        ) {}
    }
}

package com.nmarsollier.fitfat.measures.ui.list

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.material.FabPosition
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import com.nmarsollier.fitfat.R
import com.nmarsollier.fitfat.common.navigation.NavigationProvider
import com.nmarsollier.fitfat.common.ui.preview.KoinPreview
import com.nmarsollier.fitfat.common.ui.views.LoadingView
import com.nmarsollier.fitfat.measures.model.Measure
import com.nmarsollier.fitfat.measures.samples.Samples
import com.nmarsollier.fitfat.userSettings.model.UserSettings
import com.nmarsollier.fitfat.userSettings.samples.Samples
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MeasuresListScreen(
    viewModel: MeasuresListViewModel = koinViewModel(),
    navigationProvider: NavigationProvider = koinInject(),
) {
    val state by viewModel.state.collectAsState(viewModel.viewModelScope.coroutineContext)
    val event by viewModel.event.collectAsState(null)

    LaunchedEffect(viewModel) {
        viewModel.reduce(MeasuresListAction.Initialize)
    }

    LaunchedEffect(event) {
        when (val e = event) {
            is MeasuresListEvent.Redirect -> {
                when (val dst = e.destination) {
                    is Destination.NewMeasure -> navigationProvider.appNavActions?.navigateNewMeasure()

                    is Destination.ViewMeasure -> navigationProvider.appNavActions?.navigateEditMeasure(
                        dst.measure
                    )
                }
            }

            else -> Unit
        }
    }

    MeasuresListContent(state, viewModel::reduce)
}

@OptIn(ExperimentalFoundationApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
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
        ) {
            Icon(
                painterResource(id = R.drawable.ic_add_black_24dp),
                "",
            )
        }
    }, floatingActionButtonPosition = FabPosition.End
    ) {
        Column {
            when (state) {
                MeasuresListState.Loading -> LoadingView()

                is MeasuresListState.Ready -> LazyColumn(Modifier.fillMaxSize()) {
                    items(state.measures) {
                        MeasureItemView(state.userSettings, it, reduce)
                        Divider(color = Color.LightGray, thickness = 1.dp)
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
            MeasuresListState.Ready(UserSettings.Samples.simpleData.value,
                Measure.Samples.simpleData.map { it.value }),
            MeasuresListViewModel.Samples::reduce
        )
    }
}

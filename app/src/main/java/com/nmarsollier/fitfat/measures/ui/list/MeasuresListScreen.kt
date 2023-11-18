package com.nmarsollier.fitfat.measures.ui.list

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewModelScope
import com.nmarsollier.fitfat.R
import com.nmarsollier.fitfat.measures.model.Measure
import com.nmarsollier.fitfat.measures.ui.edit.EditMeasureActivity
import com.nmarsollier.fitfat.measures.samples.Samples
import com.nmarsollier.fitfat.userSettings.model.UserSettings
import com.nmarsollier.fitfat.userSettings.samples.Samples
import com.nmarsollier.fitfat.common.ui.preview.KoinPreview
import com.nmarsollier.fitfat.common.ui.theme.AppColors
import com.nmarsollier.fitfat.common.ui.views.LoadingView
import org.koin.androidx.compose.koinViewModel

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MeasuresListScreen(
    viewModel: MeasuresListViewModel = koinViewModel()
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val state by viewModel.state.collectAsState(viewModel.viewModelScope.coroutineContext)

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    viewModel.load()
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

    MeasuresListContent(state, viewModel)
}

@OptIn(ExperimentalFoundationApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MeasuresListContent(
    state: MeasuresListState, reducer: MeasuresListReducer
) {
    val context = LocalContext.current

    Scaffold(topBar = {
        MeasuresListMenu()
    }, floatingActionButton = {
        FloatingActionButton(
            onClick = {
                reducer.openNewMeasure()
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
        Column(
            modifier = Modifier.background(AppColors.background)
        ) {
            when (state) {
                MeasuresListState.Loading -> LoadingView()

                is MeasuresListState.Ready -> LazyColumn(Modifier.fillMaxSize()) {
                    items(state.measures) {
                        MeasureItemView(state.userSettings, it, reducer)
                        Divider(color = Color.LightGray, thickness = 1.dp)
                    }
                }

                is MeasuresListState.Redirect -> when (val dst = state.destination) {
                    is Destination.NewMeasure -> EditMeasureActivity.startActivity(context)
                    is Destination.ViewMeasure -> EditMeasureActivity.startActivity(
                        context, dst.measure
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun MeasuresListScreenPreview() {
    KoinPreview {
        MeasuresListContent(
            MeasuresListState.Ready(
                UserSettings.Samples.simpleData.value,
                Measure.Samples.simpleData.map { it.value }
            ), MeasuresListViewModel.Samples.reducer()
        )
    }
}

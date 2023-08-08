package com.nmarsollier.fitfat.ui.userSettings

import android.widget.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.*
import com.nmarsollier.fitfat.R
import com.nmarsollier.fitfat.ui.common.views.*
import com.nmarsollier.fitfat.utils.*
import org.koin.androidx.compose.*

@Composable
fun OptionsScreen(
    viewModel: OptionsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val event by viewModel.event.collectAsState(null)

    val name = remember(state) {
        mutableStateOf(
            (state as? OptionsState.Ready)?.userSettings?.displayName ?: ""
        )
    }

    val weight = remember(state) {
        mutableStateOf(
            (state as? OptionsState.Ready)?.userSettings?.displayWeight()?.formatString() ?: ""
        )
    }

    val height = remember(state) {
        mutableStateOf(
            (state as? OptionsState.Ready)?.userSettings?.displayHeight()?.formatString() ?: ""
        )
    }

    fun save() {
        viewModel.reduce(OptionsAction.UpdateDisplayName(name.value))
        viewModel.reduce(OptionsAction.UpdateWeight(weight.value.toDoubleOrNull() ?: 0.0))
        viewModel.reduce(OptionsAction.UpdateHeight(height.value.toDoubleOrNull() ?: 0.0))
        viewModel.reduce(OptionsAction.SaveSettings)
    }

    OnPauseEffect {
        save()
    }

    when (event) {
        OptionsEvent.ShowGoogleLoginError -> {
            Toast.makeText(
                LocalContext.current, stringResource(id = R.string.google_error), Toast.LENGTH_SHORT
            ).show()
        }

        else -> Unit
    }

    Scaffold(topBar = {
        OptionsMenu(onSaveClick = { save() })
    }) {
        when (val st = state) {
            OptionsState.Loading -> LoadingView()
            is OptionsState.Ready -> OptionsContentDetail(
                modifier = Modifier.padding(it),
                state = st,
                nameState = name,
                heightState = height,
                weightState = weight,
                reduce = viewModel::reduce
            )
        }
    }
}

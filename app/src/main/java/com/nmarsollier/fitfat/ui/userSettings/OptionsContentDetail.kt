package com.nmarsollier.fitfat.ui.userSettings

import androidx.activity.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.nmarsollier.fitfat.R
import com.nmarsollier.fitfat.models.userSettings.*
import com.nmarsollier.fitfat.models.userSettings.db.*
import com.nmarsollier.fitfat.ui.common.dialogs.*
import com.nmarsollier.fitfat.ui.common.preview.*
import com.nmarsollier.fitfat.ui.common.viewModel.reduceWith
import com.nmarsollier.fitfat.utils.*

@Composable
fun OptionsContentDetail(
    modifier: Modifier = Modifier,
    state: OptionsState.Ready,
    nameState: MutableState<String>,
    heightState: MutableState<String>,
    weightState: MutableState<String>,
    reducer: (OptionsAction) -> Unit,
) {
    val activity = LocalContext.current as? ComponentActivity
    val context = LocalContext.current

    val switchColors = SwitchDefaults.colors(
        checkedThumbColor = MaterialTheme.colorScheme.primary,
        checkedTrackColor = MaterialTheme.colorScheme.surface,
        checkedBorderColor = MaterialTheme.colorScheme.primary,
        uncheckedThumbColor = MaterialTheme.colorScheme.surface,
        uncheckedTrackColor = MaterialTheme.colorScheme.secondaryContainer,
        uncheckedBorderColor = MaterialTheme.colorScheme.secondaryContainer,
    )

    val userSettings by rememberUpdatedState(state.userSettings)
    val measureType by rememberUpdatedState(userSettings.measureSystem)

    var name by nameState
    var weight by weightState
    var height by heightState

    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
            .verticalScroll(rememberScrollState(), enabled = true),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text(stringResource(R.string.options_display_name)) },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.background,
                disabledContainerColor = MaterialTheme.colorScheme.background,
                unfocusedContainerColor = MaterialTheme.colorScheme.background
            )
        )
        TextField(
            value = userSettings.birthDate.formatDate,
            onValueChange = {},
            readOnly = true,
            enabled = false,
            label = { Text(stringResource(R.string.options_birth_date)) },
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    showDatePicker(
                        context,
                        userSettings.birthDate
                    ) {
                        OptionsAction
                            .UpdateBirthDate(it)
                            .reduceWith(reducer)
                    }
                },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.background,
                disabledContainerColor = MaterialTheme.colorScheme.background,
                unfocusedContainerColor = MaterialTheme.colorScheme.background
            )
        )
        Column {
            Text(
                stringResource(R.string.options_system_of_measurement_metric),
                color = MaterialTheme.colorScheme.onSurface
            )
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(selected = userSettings.measureSystem == MeasureType.METRIC,
                    onClick = {
                        OptionsAction.UpdateMeasureSystem(MeasureType.METRIC)
                            .reduceWith(reducer)
                    })
                Text(
                    stringResource(R.string.options_system_of_metric),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(16.dp))
                RadioButton(selected = userSettings.measureSystem == MeasureType.IMPERIAL,
                    onClick = {
                        OptionsAction.UpdateMeasureSystem(MeasureType.IMPERIAL)
                            .reduceWith(reducer)
                    })
                Text(
                    stringResource(R.string.options_system_of_imperial),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
        Column {
            Text(
                "Sex",
                color = MaterialTheme.colorScheme.onSurface
            )
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(selected = userSettings.sex == SexType.MALE,
                    onClick = { OptionsAction.UpdateSex(SexType.MALE).reduceWith(reducer) })
                Text(
                    stringResource(R.string.options_sex_male),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(16.dp))
                RadioButton(selected = userSettings.sex == SexType.FEMALE,
                    onClick = { OptionsAction.UpdateSex(SexType.FEMALE).reduceWith(reducer) })
                Text(
                    stringResource(R.string.options_sex_female),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = weight,
                onValueChange = { weight = it },
                label = { Text(stringResource(R.string.options_weight)) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    disabledContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedContainerColor = MaterialTheme.colorScheme.background
                )
            )
            Text(
                stringResource(measureType.weightResId),
                modifier = Modifier.padding(start = 5.dp),
                color = MaterialTheme.colorScheme.primary
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = height,
                onValueChange = { height = it },
                label = { Text(stringResource(R.string.options_height)) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    disabledContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedContainerColor = MaterialTheme.colorScheme.background
                )
            )
            Text(
                stringResource(measureType.heightResId),
                modifier = Modifier.padding(start = 5.dp),
                color = MaterialTheme.colorScheme.primary
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                stringResource(R.string.save_my_data_in_cloud),
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onSurface
            )
            Switch(
                checked = !userSettings.firebaseToken.isNullOrEmpty(),
                onCheckedChange = {
                    activity ?: return@Switch
                    if (it) {
                        OptionsAction.LoginWithGoogle(activity)
                    } else {
                        OptionsAction.DisableFirebase
                    }.reduceWith(reducer)
                },
                colors = switchColors
            )
        }
    }
}

@Preview
@Composable
private fun OptionsContentDetailPreview() {
    KoinPreview {
        OptionsContentDetail(
            state = OptionsState.Ready(
                userSettings = UserSettings.Samples.simpleData,
                hasChanged = false,
            ),
            nameState = remember { mutableStateOf("Nestor") },
            heightState = remember { mutableStateOf("0.00") },
            weightState = remember { mutableStateOf("0.00") }
        ) {}
    }
}

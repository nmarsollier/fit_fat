package com.nmarsollier.fitfat.userSettings.ui

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.RadioButton
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nmarsollier.fitfat.R
import com.nmarsollier.fitfat.common.converters.formatDate
import com.nmarsollier.fitfat.common.converters.formatString
import com.nmarsollier.fitfat.userSettings.model.UserSettings
import com.nmarsollier.fitfat.userSettings.model.db.UserSettingsData
import com.nmarsollier.fitfat.userSettings.samples.Samples

@Composable
fun OptionsContentDetail(
    state: OptionsState.Ready, reduce: (OptionsEvent) -> Unit
) {
    val activity = LocalContext.current as? ComponentActivity
    val userSettings = state.userSettings
    val measureType = userSettings.measureSystem
    val context = LocalContext.current

    Column(
        Modifier
            .padding(16.dp)
            .background(com.nmarsollier.fitfat.common.ui.theme.AppColors.background)
            .verticalScroll(rememberScrollState(), enabled = true),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        TextField(
            value = userSettings.displayName,
            onValueChange = { reduce(OptionsEvent.UpdateDisplayName(it)) },
            label = { Text(stringResource(R.string.options_display_name)) },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.textFieldColors(
                disabledLabelColor = Color.Black,
                disabledTextColor = Color.Black,
                backgroundColor = Color.Transparent
            )
        )
        TextField(
            value = userSettings.birthDate.formatDate(),
            onValueChange = {},
            readOnly = true,
            enabled = false,
            label = { Text(stringResource(R.string.options_birth_date)) },
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    com.nmarsollier.fitfat.common.ui.dialogs.showDatePicker(
                        context,
                        userSettings.birthDate
                    ) {
                        reduce(OptionsEvent.UpdateBirthDate(it))
                    }
                },
            colors = TextFieldDefaults.textFieldColors(
                disabledLabelColor = Color.Black,
                disabledTextColor = Color.Black,
                backgroundColor = Color.Transparent
            ),
        )
        Column {
            Text(stringResource(R.string.options_system_of_measurement_metric))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(selected = userSettings.measureSystem == UserSettingsData.MeasureType.METRIC,
                    onClick = { reduce(OptionsEvent.UpdateMeasureSystem(UserSettingsData.MeasureType.METRIC)) })
                Text("Metric")
                Spacer(modifier = Modifier.width(16.dp))
                RadioButton(selected = userSettings.measureSystem == UserSettingsData.MeasureType.IMPERIAL,
                    onClick = { reduce(OptionsEvent.UpdateMeasureSystem(UserSettingsData.MeasureType.IMPERIAL)) })
                Text("Imperial")
            }
        }
        Column {
            Text(
                "Sex"
            )
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(selected = userSettings.sex == UserSettingsData.SexType.MALE,
                    onClick = { reduce(OptionsEvent.UpdateSex(UserSettingsData.SexType.MALE)) })
                Text(stringResource(R.string.options_sex_male))
                Spacer(modifier = Modifier.width(16.dp))
                RadioButton(selected = userSettings.sex == UserSettingsData.SexType.FEMALE,
                    onClick = { reduce(OptionsEvent.UpdateSex(UserSettingsData.SexType.FEMALE)) })
                Text(stringResource(R.string.options_sex_female))
            }
        }

        var weight by remember(userSettings.measureSystem) {
            mutableStateOf(
                userSettings.displayWeight().formatString()
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                modifier = Modifier.onFocusChanged {
                    reduce(OptionsEvent.UpdateWeight(weight.toDoubleOrNull() ?: 0.0))
                },
                value = weight,
                onValueChange = { weight = it },
                label = { Text(stringResource(R.string.options_weight)) },
                colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent)
            )
            Text(
                stringResource(measureType.weightResId),
                modifier = Modifier.padding(start = 5.dp),
                color = com.nmarsollier.fitfat.common.ui.theme.AppColors.primary
            )
        }

        var height by remember(userSettings.measureSystem) {
            mutableStateOf(
                userSettings.displayHeight().formatString()
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                modifier = Modifier.onFocusChanged {
                    reduce(OptionsEvent.UpdateHeight(height.toDoubleOrNull() ?: 0.0))
                },
                value = height,
                onValueChange = { height = it },
                label = { Text(stringResource(R.string.options_height)) },
                colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent)
            )
            Text(
                stringResource(measureType.heightResId),
                modifier = Modifier.padding(start = 5.dp),
                color = com.nmarsollier.fitfat.common.ui.theme.AppColors.primary
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                stringResource(R.string.save_my_data_in_cloud), modifier = Modifier.weight(1f)
            )
            Switch(checked = !userSettings.firebaseToken.isNullOrEmpty(), onCheckedChange = {
                if (it) {
                    activity?.let {
                        reduce(OptionsEvent.LoginWithGoogle(activity))
                    }
                } else {
                    reduce(OptionsEvent.DisableFirebase)
                }
            })
        }
    }
}

@Preview
@Composable
fun OptionsContentDetailPreview() {
    com.nmarsollier.fitfat.common.ui.preview.KoinPreview {
        OptionsContentDetail(
            OptionsState.Ready(
                userSettings = UserSettings.Samples.simpleData.value,
                hasChanged = false,
            ), OptionsViewModel.Samples::reduce
        )
    }
}

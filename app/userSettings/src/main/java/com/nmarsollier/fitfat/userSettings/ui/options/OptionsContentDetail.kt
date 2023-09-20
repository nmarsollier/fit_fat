package com.nmarsollier.fitfat.userSettings.ui.options

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nmarsollier.fitfat.userSettings.R
import com.nmarsollier.fitfat.userSettings.model.UserSettings
import com.nmarsollier.fitfat.userSettings.model.db.UserSettingsData
import com.nmarsollier.fitfat.userSettings.ui.utils.displayHeight
import com.nmarsollier.fitfat.userSettings.ui.utils.displayWeight
import com.nmarsollier.fitfat.userSettings.ui.utils.heightResId
import com.nmarsollier.fitfat.userSettings.ui.utils.preview.Samples
import com.nmarsollier.fitfat.userSettings.ui.utils.weightResId
import com.nmarsollier.fitfat.utils.converters.formatDate
import com.nmarsollier.fitfat.utils.converters.formatString
import com.nmarsollier.fitfat.utils.ui.dialogs.showDatePicker
import com.nmarsollier.fitfat.utils.ui.preview.KoinPreview
import com.nmarsollier.fitfat.utils.ui.theme.AppColors

@Composable
fun OptionsContentDetail(
    state: OptionsState.Ready, reducer: OptionsReducer
) {
    val activity = LocalContext.current as? ComponentActivity
    val userSettings = state.userSettings
    val measureType = userSettings.measureSystem
    val context = LocalContext.current

    Column(
        Modifier
            .padding(16.dp)
            .background(AppColors.background)
            .verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        TextField(
            value = userSettings.displayName,
            onValueChange = { reducer.updateDisplayName(it) },
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
                    showDatePicker(context, userSettings.birthDate) {
                        reducer.updateBirthDate(it)
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
                    onClick = { reducer.updateMeasureSystem(UserSettingsData.MeasureType.METRIC) })
                Text("Metric")
                Spacer(modifier = Modifier.width(16.dp))
                RadioButton(selected = userSettings.measureSystem == UserSettingsData.MeasureType.IMPERIAL,
                    onClick = { reducer.updateMeasureSystem(UserSettingsData.MeasureType.IMPERIAL) })
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
                    onClick = { reducer.updateSex(UserSettingsData.SexType.MALE) })
                Text(stringResource(R.string.options_sex_male))
                Spacer(modifier = Modifier.width(16.dp))
                RadioButton(selected = userSettings.sex == UserSettingsData.SexType.FEMALE,
                    onClick = { reducer.updateSex(UserSettingsData.SexType.FEMALE) })
                Text(stringResource(R.string.options_sex_female))
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = userSettings.displayWeight().formatString(),
                onValueChange = { reducer.updateWeight(it.toDoubleOrNull() ?: 0.0) },
                label = { Text(stringResource(R.string.options_weight)) },
                colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent)
            )
            Text(
                stringResource(measureType.weightResId),
                modifier = Modifier.padding(start = 5.dp),
                color = AppColors.primary
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = userSettings.displayHeight().formatString(),
                onValueChange = { reducer.updateHeight(it.toDoubleOrNull() ?: 0.0) },
                label = { Text(stringResource(R.string.options_height)) },
                colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent)
            )
            Text(
                stringResource(measureType.heightResId),
                modifier = Modifier.padding(start = 5.dp),
                color = AppColors.primary
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
                        reducer.loginWithGoogle(activity)
                    }
                } else {
                    reducer.disableFirebase()
                }
            })
        }
    }
}

@Preview
@Composable
fun OptionsContentDetailPreview() {
    KoinPreview {
        OptionsContentDetail(
            OptionsState.Ready(
                userSettings = UserSettings.Samples.simpleData.value,
                hasChanged = false,
            ), OptionsViewModel.Samples.reducer()
        )
    }
}

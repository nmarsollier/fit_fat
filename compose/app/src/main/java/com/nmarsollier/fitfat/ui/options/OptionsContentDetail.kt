package com.nmarsollier.fitfat.ui.options

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
import com.nmarsollier.fitfat.R
import com.nmarsollier.fitfat.model.userSettings.UserSettingsEntity
import com.nmarsollier.fitfat.model.userSettings.UserSettingsEntity.MeasureType
import com.nmarsollier.fitfat.model.userSettings.UserSettingsEntity.SexType
import com.nmarsollier.fitfat.ui.common.AppColors
import com.nmarsollier.fitfat.ui.common.displayHeight
import com.nmarsollier.fitfat.ui.common.displayWeight
import com.nmarsollier.fitfat.ui.common.heightResId
import com.nmarsollier.fitfat.ui.common.showDatePicker
import com.nmarsollier.fitfat.ui.common.weightResId
import com.nmarsollier.fitfat.utils.KoinPreview
import com.nmarsollier.fitfat.utils.Samples
import com.nmarsollier.fitfat.utils.formatDate
import com.nmarsollier.fitfat.utils.formatString

@Composable
fun OptionsContentDetail(
    state: OptionsState.Ready, reducer: OptionsReducer
) {
    val activity = LocalContext.current as? ComponentActivity
    val userSettings = state.userSettingsEntity
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
                RadioButton(selected = userSettings.measureSystem == MeasureType.METRIC,
                    onClick = { reducer.updateMeasureSystem(MeasureType.METRIC) })
                Text("Metric")
                Spacer(modifier = Modifier.width(16.dp))
                RadioButton(selected = userSettings.measureSystem == MeasureType.IMPERIAL,
                    onClick = { reducer.updateMeasureSystem(MeasureType.IMPERIAL) })
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
                RadioButton(selected = userSettings.sex == SexType.MALE,
                    onClick = { reducer.updateSex(SexType.MALE) })
                Text(stringResource(R.string.options_sex_male))
                Spacer(modifier = Modifier.width(16.dp))
                RadioButton(selected = userSettings.sex == SexType.FEMALE,
                    onClick = { reducer.updateSex(SexType.FEMALE) })
                Text(stringResource(R.string.options_sex_female))
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = userSettings.displayWeight(userSettings.weight).formatString(),
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
                value = userSettings.displayHeight(userSettings.height).formatString(),
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
                userSettingsEntity = UserSettingsEntity.Samples.simpleData,
                hasChanged = false,
            ), OptionsViewModel.Samples.reducer()
        )
    }
}

package com.nmarsollier.fitfat.measures.ui.edit

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nmarsollier.fitfat.R
import com.nmarsollier.fitfat.measures.model.Measure
import com.nmarsollier.fitfat.measures.model.db.MeasureData
import com.nmarsollier.fitfat.measures.model.db.MeasureValue
import com.nmarsollier.fitfat.measures.model.isRequiredForMethod
import com.nmarsollier.fitfat.measures.ui.labelRes
import com.nmarsollier.fitfat.measures.samples.Samples
import com.nmarsollier.fitfat.userSettings.model.UserSettings
import com.nmarsollier.fitfat.userSettings.model.db.UserSettingsData
import com.nmarsollier.fitfat.userSettings.samples.Samples
import com.nmarsollier.fitfat.common.converters.formatDate
import com.nmarsollier.fitfat.common.converters.formatString
import com.nmarsollier.fitfat.common.ui.dialogs.showDatePicker
import com.nmarsollier.fitfat.common.ui.preview.KoinPreview
import com.nmarsollier.fitfat.common.ui.theme.AppColors

@Composable
fun EditMeasureDetails(
    userSettings: UserSettingsData, measure: MeasureData, reducer: EditMeasureReducer
) {
    val context = LocalContext.current

    Column(
        Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Row(modifier = Modifier
            .padding(top = 12.dp, bottom = 12.dp)
            .clickable {
                reducer.toggleShowMethod()
            }) {
            Text(stringResource(measure.measureMethod.labelRes))

            Image(
                painterResource(R.drawable.ic_arrow_drop_down_black_24dp), contentDescription = ""
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 24.dp)
        ) {
            TextField(
                value = userSettings.birthDate.formatDate(),
                onValueChange = {},
                readOnly = true,
                enabled = false,
                label = { Text(stringResource(R.string.new_measure_date)) },
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .clickable {
                        showDatePicker(context, userSettings.birthDate) {
                            reducer.updateDate(it)
                        }
                    },
                colors = TextFieldDefaults.textFieldColors(
                    disabledLabelColor = Color.Black,
                    disabledTextColor = Color.Black,
                    backgroundColor = Color.Transparent
                ),
            )
            Spacer(Modifier.weight(1f))
            Row(
                horizontalArrangement = Arrangement.spacedBy(3.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = "${stringResource(R.string.measure_fat)} :"
                )
                Text(
                    text = measure.fatPercent.formatString()
                )
                Text(
                    text = "%"
                )
            }
        }

        val method = measure.measureMethod
        val measures = MeasureValue.values().filter { it.isRequiredForMethod(method) }
        measures.forEach {
            Row {
                when (it.inputType) {
                    MeasureValue.InputType.INT -> IntMeasureView(userSettings, measure, it, reducer)
                    MeasureValue.InputType.DOUBLE -> DecimalMeasureView(
                        userSettings,
                        measure,
                        it,
                        reducer
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun EditMeasureDetailsPreview() {
    KoinPreview {
        Column(
            modifier = Modifier.background(AppColors.background)
        ) {
            EditMeasureDetails(
                UserSettings.Samples.simpleData.value,
                Measure.Samples.simpleData[0].value,
                EditMeasureViewModel.Samples.reducer()
            )
        }
    }
}

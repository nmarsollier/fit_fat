package com.nmarsollier.fitfat.measures.ui.edit

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nmarsollier.fitfat.R
import com.nmarsollier.fitfat.measures.model.Measure
import com.nmarsollier.fitfat.measures.model.db.MeasureData
import com.nmarsollier.fitfat.measures.model.db.MeasureValue
import com.nmarsollier.fitfat.measures.ui.calculateDecimalPart
import com.nmarsollier.fitfat.measures.ui.calculateIntPart
import com.nmarsollier.fitfat.measures.ui.displayValue
import com.nmarsollier.fitfat.measures.ui.helpRes
import com.nmarsollier.fitfat.measures.samples.Samples
import com.nmarsollier.fitfat.userSettings.model.UserSettings
import com.nmarsollier.fitfat.userSettings.model.db.UserSettingsData
import com.nmarsollier.fitfat.userSettings.samples.Samples
import com.nmarsollier.fitfat.userSettings.ui.weightResId
import com.nmarsollier.fitfat.common.converters.formatString
import com.nmarsollier.fitfat.common.ui.preview.KoinPreview
import com.nmarsollier.fitfat.common.ui.theme.AppColors
import com.nmarsollier.fitfat.common.ui.viewModel.Reducer

@Composable
fun DecimalMeasureView(
    userSettings: UserSettingsData,
    measure: MeasureData,
    measureValue: MeasureValue,
    reducer: Reducer<EditMeasureEvent>
) {
    Column(
        modifier = Modifier.background(AppColors.background)
    ) {
        val unit = when (measureValue.unitType) {
            MeasureValue.UnitType.PERCENT -> stringResource(
                R.string.unit_percent
            )

            MeasureValue.UnitType.WEIGHT -> stringResource(
                userSettings.measureSystem.weightResId
            )

            MeasureValue.UnitType.WIDTH -> stringResource(R.string.unit_mm)
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(stringResource(measureValue.titleRes))
            Text(
                text = measure.displayValue(measureValue, userSettings).formatString(),
                color = colorResource(id = R.color.colorPrimary)
            )
            Text(unit)
            Spacer(Modifier.weight(1f))
            Image(painterResource(R.drawable.ic_help_outline_black_24dp),
                "",
                colorFilter = ColorFilter.tint(colorResource(R.color.colorPrimary)),
                modifier = Modifier.clickable {
                    measureValue.helpRes?.let {
                        reducer.reduce(EditMeasureEvent.ToggleHelp(it))
                    }
                })
        }

        val currentValue = measure.displayValue(measureValue, userSettings)
        Slider(value = currentValue.toInt().toFloat(),
            valueRange = 0f..measureValue.maxScale.toFloat(),
            onValueChange = {
                reducer.reduce(
                    EditMeasureEvent.UpdateMeasureValue(
                        measureValue,
                        measure.calculateIntPart(it.toInt(), measureValue, userSettings)
                    )
                )
            })

        Slider(value = ((currentValue - currentValue.toInt()) * 10).toFloat(),
            valueRange = 0f..10f,
            onValueChange = {
                reducer.reduce(
                    EditMeasureEvent.UpdateMeasureValue(
                        measureValue,
                        measure.calculateDecimalPart(it.toInt(), measureValue, userSettings)
                    )
                )
            })
    }
}

@Preview
@Composable
fun DecimalMeasureViewPreview() {
    KoinPreview {
        DecimalMeasureView(
            UserSettings.Samples.simpleData.value,
            Measure.Samples.bodyFat.value,
            MeasureValue.BODY_FAT,
            EditMeasureView.Samples.reducer()
        )
    }
}

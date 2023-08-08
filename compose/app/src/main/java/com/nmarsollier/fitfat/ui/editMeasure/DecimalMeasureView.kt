package com.nmarsollier.fitfat.ui.editMeasure

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
import com.nmarsollier.fitfat.model.measures.Measure
import com.nmarsollier.fitfat.model.measures.MeasureValue
import com.nmarsollier.fitfat.model.measures.MeasureValue.UnitType
import com.nmarsollier.fitfat.model.userSettings.UserSettingsEntity
import com.nmarsollier.fitfat.ui.common.AppColors
import com.nmarsollier.fitfat.ui.common.helpRes
import com.nmarsollier.fitfat.ui.common.weightResId
import com.nmarsollier.fitfat.utils.KoinPreview
import com.nmarsollier.fitfat.utils.Samples
import com.nmarsollier.fitfat.utils.formatString

@Composable
fun DecimalMeasureView(
    userSettingsEntity: UserSettingsEntity,
    measure: Measure,
    measureValue: MeasureValue,
    reducer: EditMeasureReducer
) {
    Column(
        modifier = Modifier.background(AppColors.background)
    ) {
        val unit = when (measureValue.unitType) {
            UnitType.PERCENT -> stringResource(R.string.unit_percent)
            UnitType.WEIGHT -> stringResource(userSettingsEntity.measureSystem.weightResId)
            UnitType.WIDTH -> stringResource(R.string.unit_mm)
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(stringResource(measureValue.titleRes))
            Text(
                text = measure.getValueForMethod(measureValue).toDouble().formatString(),
                color = colorResource(id = R.color.colorPrimary)
            )
            Text(unit)
            Spacer(Modifier.weight(1f))
            Image(painterResource(R.drawable.ic_help_outline_black_24dp),
                "",
                colorFilter = ColorFilter.tint(colorResource(R.color.colorPrimary)),
                modifier = Modifier.clickable {
                    measureValue.helpRes?.let {
                        reducer.toggleHelp(it)
                    }
                })
        }

        val currentValue = measure.getValueForMethod(measureValue).toDouble()
        Slider(value = currentValue.toInt().toFloat(),
            valueRange = 0f..measureValue.maxScale.toFloat(),
            onValueChange = {
                reducer.updateMeasureValue(
                    measureValue, measure.calculateIntPart(it.toInt(), measureValue)
                )
            })

        Slider(value = ((currentValue - currentValue.toInt()) * 10).toFloat(),
            valueRange = 0f..10f,
            onValueChange = {
                reducer.updateMeasureValue(
                    measureValue, measure.calculateDecimalPart(it.toInt(), measureValue)
                )
            })
    }
}


private fun Measure.calculateIntPart(
    progress: Int, measureValue: MeasureValue
): Double {
    val currentValue = getValueForMethod(measureValue).toDouble()
    val decimal = currentValue - currentValue.toInt()
    return progress.toDouble() + decimal
}


private fun Measure.calculateDecimalPart(
    progress: Int, measureValue: MeasureValue
): Double {
    val currentValue = getValueForMethod(measureValue).toInt()

    return currentValue.toDouble() + (progress.toDouble() / 10.0)
}

@Preview
@Composable
fun DecimalMeasureViewPreview() {
    KoinPreview {
        DecimalMeasureView(
            UserSettingsEntity.Samples.simpleData,
            Measure.Samples.bodyFat,
            MeasureValue.BODY_FAT,
            EditMeasureViewModel.Samples.reducer()
        )
    }
}

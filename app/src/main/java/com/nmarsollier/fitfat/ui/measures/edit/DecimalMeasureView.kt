package com.nmarsollier.fitfat.ui.measures.edit

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.nmarsollier.fitfat.R
import com.nmarsollier.fitfat.models.measures.*
import com.nmarsollier.fitfat.models.measures.db.*
import com.nmarsollier.fitfat.models.userSettings.*
import com.nmarsollier.fitfat.ui.common.preview.*
import com.nmarsollier.fitfat.ui.measures.*
import com.nmarsollier.fitfat.ui.userSettings.*
import com.nmarsollier.fitfat.utils.*

@Composable
fun DecimalMeasureView(
    userSettings: UserSettings,
    measure: Measure,
    measureValue: MeasureValue,
    reduce: (EditMeasureAction) -> Unit
) {
    Column {
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
                color = MaterialTheme.colorScheme.primary
            )
            Text(unit)
            Spacer(Modifier.weight(1f))
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Info",
                tint = MaterialTheme.colorScheme.primary
            )
        }

        val currentValue = measure.displayValue(measureValue, userSettings)
        Slider(value = currentValue.toInt().toFloat(),
            valueRange = 0f..measureValue.maxScale.toFloat(),
            onValueChange = {
                reduce(
                    EditMeasureAction.UpdateMeasureValue(
                        measureValue,
                        measure.calculateIntPart(it.toInt(), measureValue, userSettings)
                    )
                )
            })

        Slider(value = ((currentValue - currentValue.toInt()) * 10).toFloat(),
            valueRange = 0f..10f,
            onValueChange = {
                reduce(
                    EditMeasureAction.UpdateMeasureValue(
                        measureValue,
                        measure.calculateDecimalPart(it.toInt(), measureValue, userSettings)
                    )
                )
            })
    }
}

@Preview
@Composable
private fun DecimalMeasureViewPreview() {
    KoinPreview {
        DecimalMeasureView(
            UserSettings.Samples.simpleData,
            Measure.Samples.bodyFat,
            MeasureValue.BODY_FAT,
        ) {}
    }
}

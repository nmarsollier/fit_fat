package com.nmarsollier.fitfat.ui.measures.edit

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.*
import androidx.compose.ui.tooling.preview.*
import com.nmarsollier.fitfat.R
import com.nmarsollier.fitfat.models.measures.*
import com.nmarsollier.fitfat.models.measures.db.*
import com.nmarsollier.fitfat.models.userSettings.*
import com.nmarsollier.fitfat.ui.common.preview.*
import com.nmarsollier.fitfat.ui.measures.*
import com.nmarsollier.fitfat.ui.userSettings.*
import com.nmarsollier.fitfat.utils.*

@Composable
fun IntMeasureView(
    userSettings: UserSettings,
    measure: Measure,
    measureValue: MeasureValue,
    reduce: (EditMeasureAction) -> Unit
) {
    val unit = stringResource(
        when (measureValue.unitType) {
            MeasureValue.UnitType.PERCENT -> R.string.unit_percent
            MeasureValue.UnitType.WEIGHT -> userSettings.measureSystem.weightResId
            MeasureValue.UnitType.WIDTH -> R.string.unit_mm
        }
    )

    val primaryStyle = MaterialTheme.typography.bodyMedium.toSpanStyle()
        .copy(color = MaterialTheme.colorScheme.primary)

    val titleString = stringResource(measureValue.titleRes)
    val currValue = measure.displayValue(measureValue, userSettings).toInt()
    val weightString = remember(currValue) {
        buildAnnotatedString {
            append("$titleString : ")
            withStyle(style = primaryStyle) {
                append(currValue.formatString())
            }
            append(unit)
        }
    }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(weightString)
            Spacer(Modifier.weight(1f))
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Info",
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Slider(
            value = measure.displayValue(measureValue, userSettings).toFloat(),
            valueRange = 0f..measureValue.maxScale.toFloat(),
            onValueChange = {
                reduce(
                    EditMeasureAction.UpdateMeasureValue(
                        measureValue, it.toInt()
                    )
                )
            })
    }
}

@Preview
@Composable
private fun IntMeasureValuePreview() {
    KoinPreview {
        Column {
            IntMeasureView(
                UserSettings.Samples.simpleData,
                Measure.Samples.simpleData[0],
                MeasureValue.ABDOMINAL
            ) {}
        }
    }
}

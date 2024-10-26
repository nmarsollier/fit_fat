package com.nmarsollier.fitfat.ui.measures.list

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.nmarsollier.fitfat.R
import com.nmarsollier.fitfat.models.measures.*
import com.nmarsollier.fitfat.models.userSettings.*
import com.nmarsollier.fitfat.ui.common.preview.*
import com.nmarsollier.fitfat.ui.common.theme.*
import com.nmarsollier.fitfat.ui.common.viewModel.reduceWith
import com.nmarsollier.fitfat.ui.measures.*
import com.nmarsollier.fitfat.ui.userSettings.*
import com.nmarsollier.fitfat.utils.*

@Composable
@ExperimentalFoundationApi
fun MeasureItemView(
    modifier: Modifier = Modifier,
    userSettings: UserSettings,
    measure: Measure,
    reducer: (MeasuresListAction) -> Unit
) {
    val weightUnit = stringResource(userSettings.measureSystem.weightResId)

    val primaryStyle = MaterialTheme.typography.bodyMedium.toSpanStyle()
        .copy(color = MaterialTheme.colorScheme.primary)

    val measureWeightString = stringResource(R.string.measure_weight)
    val weightString = remember(measure.uid) {
        buildAnnotatedString {
            append("$measureWeightString : ")
            withStyle(style = primaryStyle) {
                append(userSettings.displayWeight(measure.bodyWeight).formatString())
            }
            append(" $weightUnit")
        }
    }

    val fatStringResource = stringResource(R.string.measure_fat)
    val fatPercentString = remember(measure.uid) {
        if (measure.showFatPercent) {
            buildAnnotatedString {
                append("$fatStringResource : ")
                withStyle(style = primaryStyle) {
                    append(measure.bodyFatMass.formatString())
                }
                append("$weightUnit /")
                withStyle(style = primaryStyle) {
                    append(measure.fatPercent.formatString())
                }
                append("%")
            }
        } else {
            null
        }
    }

    val freeFatMassStringResource = stringResource(R.string.free_fat_mass)
    val freeFatMassString = remember(measure.uid) {
        if (measure.showFreeFatMass) {
            buildAnnotatedString {
                append("$freeFatMassStringResource : ")
                withStyle(style = primaryStyle) {
                    append(measure.leanWeight.formatString())
                }
                append(" $weightUnit")
            }
        } else {
            null
        }
    }

    val fmmiStringResource = stringResource(R.string.fmmi)
    val fmmiString = remember(measure.uid) {
        if (measure.showFMMI) {
            buildAnnotatedString {
                append("$fmmiStringResource : ")
                withStyle(style = primaryStyle) {
                    append(measure.freeFatMassIndex.formatString())
                }
            }
        } else {
            null
        }
    }

    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
            .combinedClickable(onClick = {
                MeasuresListAction
                    .OpenViewMeasure(measure)
                    .reduceWith(reducer)
            })
            .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                style = GlobalTextStyles.bodyOnSurface,
                text = measure.date.formatDateTime,
            )
            Text(
                style = GlobalTextStyles.bodyOnSurface,
                text = stringResource(id = measure.measureMethod.labelRes),
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                style = GlobalTextStyles.bodyOnSurface, text = weightString
            )
            if (fatPercentString != null) {
                Text(
                    style = GlobalTextStyles.bodyOnSurface,
                    text = fatPercentString,
                )
            }
        }

        if (freeFatMassString != null || fmmiString != null) {
            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                if (freeFatMassString != null) {
                    Text(
                        style = GlobalTextStyles.bodyOnSurface,
                        text = freeFatMassString,
                    )
                }

                if (fmmiString != null) {
                    Text(
                        style = GlobalTextStyles.bodyOnSurface,
                        text = fmmiString,
                    )
                }
            }
        }
    }
}

inline val Measure.showFreeFatMass
    get() = leanWeight > 0

inline val Measure.showFMMI
    get() = freeFatMassIndex > 0

inline val Measure.showFatPercent
    get() = fatPercent > 0

@ExperimentalFoundationApi
@Preview
@Composable
private fun MeasureItemViewPreview() {
    KoinPreview {
        Column {
            MeasureItemView(
                userSettings = UserSettings.Samples.simpleData,
                measure = Measure.Samples.simpleData[0]
            ) {}
        }
    }
}

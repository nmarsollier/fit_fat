package com.nmarsollier.fitfat.measures.ui.list

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nmarsollier.fitfat.R
import com.nmarsollier.fitfat.measures.model.Measure
import com.nmarsollier.fitfat.measures.model.bodyFatMass
import com.nmarsollier.fitfat.measures.model.db.MeasureData
import com.nmarsollier.fitfat.measures.model.freeFatMassIndex
import com.nmarsollier.fitfat.measures.model.leanWeight
import com.nmarsollier.fitfat.measures.ui.labelRes
import com.nmarsollier.fitfat.measures.samples.Samples
import com.nmarsollier.fitfat.userSettings.model.UserSettings
import com.nmarsollier.fitfat.userSettings.model.db.UserSettingsData
import com.nmarsollier.fitfat.userSettings.ui.displayWeight
import com.nmarsollier.fitfat.userSettings.samples.Samples
import com.nmarsollier.fitfat.userSettings.ui.weightResId
import com.nmarsollier.fitfat.common.converters.formatDateTime
import com.nmarsollier.fitfat.common.converters.formatString
import com.nmarsollier.fitfat.common.ui.preview.KoinPreview
import com.nmarsollier.fitfat.common.ui.theme.AppColors

@Composable
@ExperimentalFoundationApi
fun MeasureItemView(
    userSettings: UserSettingsData,
    measure: MeasureData,
    reduce: (MeasuresListEvent) -> Unit
) {
    val fontSize = 14.sp

    Column(
        Modifier
            .padding(top = 16.dp, bottom = 16.dp)
            .fillMaxWidth()
            .combinedClickable(onClick = {
                reduce(MeasuresListEvent.OpenViewMeasure(measure))
            }, onLongClick = {
                reduce(MeasuresListEvent.DeleteMeasure(measure))
            }),
        verticalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = measure.date.formatDateTime(), fontSize = fontSize
            )
            Text(
                text = stringResource(id = measure.measureMethod.labelRes),
                fontSize = fontSize
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(3.dp),
            ) {
                Text(
                    text = "${stringResource(R.string.measure_weight)} :", fontSize = fontSize
                )
                Text(
                    text = userSettings.displayWeight(
                        measure.bodyWeight
                    ).formatString(), color = AppColors.primary, fontSize = fontSize
                )
                Text(
                    text = stringResource(userSettings.measureSystem.weightResId),
                    fontSize = fontSize
                )
            }

            if (measure.showFatPercent) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                ) {
                    Text(
                        text = "${stringResource(R.string.measure_fat)} :", fontSize = fontSize
                    )
                    Text(
                        text = measure.fatPercent.formatString(),
                        color = AppColors.primary,
                        fontSize = fontSize
                    )
                    Text(
                        text = "% /", fontSize = fontSize
                    )
                    Text(
                        text = userSettings.displayWeight(measure.bodyFatMass)
                            .formatString(), color = AppColors.primary, fontSize = fontSize
                    )
                    Text(
                        text = stringResource(userSettings.measureSystem.weightResId),
                        fontSize = fontSize
                    )
                }
            }
        }

        if (measure.showFreeFatMass || measure.showFMMI) {
            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                if (measure.showFreeFatMass) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                    ) {
                        Text(
                            text = "${stringResource(R.string.free_fat_mass)} :",
                            fontSize = fontSize
                        )
                        Text(
                            text = userSettings.displayWeight(measure.leanWeight)
                                .formatString(), color = AppColors.primary, fontSize = fontSize
                        )
                        Text(
                            text = stringResource(userSettings.measureSystem.weightResId),
                            fontSize = fontSize
                        )
                    }
                }

                if (measure.showFMMI) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Text(
                            text = "${stringResource(R.string.fmmi)} :", fontSize = fontSize
                        )
                        Text(
                            text = measure.freeFatMassIndex.formatString(),
                            color = AppColors.primary,
                            fontSize = fontSize
                        )
                    }
                }
            }
        }
    }
}

val MeasureData.showFreeFatMass
    get() = leanWeight > 0

val MeasureData.showFMMI
    get() = freeFatMassIndex > 0

val MeasureData.showFatPercent
    get() = fatPercent > 0

@ExperimentalFoundationApi
@Preview
@Composable
fun MeasureItemViewPreview() {
    KoinPreview {
        Column(
            modifier = Modifier.background(AppColors.background)
        ) {
            MeasureItemView(
                UserSettings.Samples.simpleData.value,
                Measure.Samples.simpleData[0].value,
                MeasuresListViewModel.Samples::reduce
            )
        }
    }
}

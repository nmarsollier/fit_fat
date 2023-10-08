package com.nmarsollier.fitfat.ui.measuresList

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
import com.nmarsollier.fitfat.model.measures.Measure
import com.nmarsollier.fitfat.model.userSettings.UserSettingsEntity
import com.nmarsollier.fitfat.ui.common.AppColors
import com.nmarsollier.fitfat.ui.common.displayWeight
import com.nmarsollier.fitfat.ui.common.labelRes
import com.nmarsollier.fitfat.ui.common.weightResId
import com.nmarsollier.fitfat.utils.KoinPreview
import com.nmarsollier.fitfat.utils.Samples
import com.nmarsollier.fitfat.utils.formatDateTime
import com.nmarsollier.fitfat.utils.formatString

@Composable
@ExperimentalFoundationApi
fun MeasureItemView(
    userSettingsEntity: UserSettingsEntity,
    measure: Measure,
    reducer: MeasuresListReducer
) {
    val fontSize = 14.sp

    Column(
        Modifier
            .padding(top = 16.dp, bottom = 16.dp)
            .fillMaxWidth()
            .combinedClickable(onClick = {
                reducer.openViewMeasure(measure)
            }, onLongClick = {
                reducer.deleteMeasure(measure)
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
                text = stringResource(id = measure.measureMethod.labelRes), fontSize = fontSize
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
                    text = userSettingsEntity.displayWeight(
                        measure.bodyWeight
                    ).formatString(), color = AppColors.primary, fontSize = fontSize
                )
                Text(
                    text = stringResource(userSettingsEntity.measureSystem.weightResId),
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
                        text = userSettingsEntity.displayWeight(measure.bodyFatMass)
                            .formatString(), color = AppColors.primary, fontSize = fontSize
                    )
                    Text(
                        text = stringResource(userSettingsEntity.measureSystem.weightResId),
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
                            text = userSettingsEntity.displayWeight(measure.leanWeight)
                                .formatString(), color = AppColors.primary, fontSize = fontSize
                        )
                        Text(
                            text = stringResource(userSettingsEntity.measureSystem.weightResId),
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

val Measure.showFreeFatMass
    get() = leanWeight > 0

val Measure.showFMMI
    get() = freeFatMassIndex > 0

val Measure.showFatPercent
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
                UserSettingsEntity.Samples.simpleData, Measure.Samples.simpleData[0],
                MeasuresListViewModel.Samples.reducer()
            )
        }
    }
}

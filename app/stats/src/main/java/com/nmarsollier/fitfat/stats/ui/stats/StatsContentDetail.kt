@file:OptIn(ExperimentalFoundationApi::class)

package com.nmarsollier.fitfat.stats.ui.stats

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nmarsollier.fitfat.measures.model.Measure
import com.nmarsollier.fitfat.measures.model.db.MeasureMethod
import com.nmarsollier.fitfat.measures.model.db.MeasureValue
import com.nmarsollier.fitfat.measures.model.isRequiredForMethod
import com.nmarsollier.fitfat.measures.ui.utils.labelRes
import com.nmarsollier.fitfat.measures.ui.utils.preview.Samples
import com.nmarsollier.fitfat.stats.ui.utils.preview.Samples
import com.nmarsollier.fitfat.userSettings.model.UserSettings
import com.nmarsollier.fitfat.userSettings.ui.utils.preview.Samples
import com.nmarsollier.fitfat.utils.R
import com.nmarsollier.fitfat.utils.ui.preview.KoinPreview

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StatsContentDetail(
    state: StatsState.Ready, reducer: StatsReducer
) {
    Column(
        Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        val values = MeasureValue.values().filter {
            if (state.selectedMethod == MeasureMethod.WEIGHT_ONLY) {
                it.isRequiredForMethod(state.selectedMethod)
            } else {
                it.isRequiredForMethod(state.selectedMethod) || it == MeasureValue.BODY_FAT
            }
        }

        Row(modifier = Modifier
            .padding(top = 12.dp, bottom = 12.dp)
            .clickable {
                reducer.toggleShowMethod()
            }) {
            Text(stringResource(state.selectedMethod.labelRes))

            Image(
                painterResource(R.drawable.ic_arrow_drop_down_black_24dp), contentDescription = ""
            )
        }

        Row {
            LazyColumn(Modifier.fillMaxSize()) {
                items(values) {
                    GraphItemView(
                        userSettings = state.userSettings,
                        measure = it,
                        graphValues = state.measures
                    )
                    Divider(color = Color.LightGray, thickness = 1.dp)
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun StatsContentDetailPreview() {
    KoinPreview {
        StatsContentDetail(
            StatsState.Ready(
                method = MeasureMethod.WEIGHT_ONLY,
                userSettings = UserSettings.Samples.simpleData.value,
                measures = Measure.Samples.simpleData.map { it.value },
                showMethod = false
            ),
            StatsViewModel.Samples.reducer()
        )
    }
}

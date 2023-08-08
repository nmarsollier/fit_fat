@file:OptIn(ExperimentalFoundationApi::class)

package com.nmarsollier.fitfat.ui.stats

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.nmarsollier.fitfat.models.measures.*
import com.nmarsollier.fitfat.models.measures.db.*
import com.nmarsollier.fitfat.models.userSettings.*
import com.nmarsollier.fitfat.ui.common.preview.*
import com.nmarsollier.fitfat.ui.measures.*
import com.nmarsollier.fitfat.ui.userSettings.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StatsContentDetail(
    state: StatsState.Ready, reduce: (StatsAction) -> Unit
) {
    Column(
        Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        val values = MeasureValue.entries.filter {
            if (state.selectedMethod == MeasureMethod.WEIGHT_ONLY) {
                it.isRequiredForMethod(state.selectedMethod)
            } else {
                it.isRequiredForMethod(state.selectedMethod) || it == MeasureValue.BODY_FAT
            }
        }

        Row(modifier = Modifier
            .padding(top = 12.dp, bottom = 12.dp)
            .clickable {
                reduce(StatsAction.ToggleShowMethod)
            }) {
            Text(stringResource(state.selectedMethod.labelRes))

            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Delete",
                tint = MaterialTheme.colorScheme.secondaryContainer
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
                    HorizontalDivider(thickness = 1.dp, color = Color.LightGray)
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun StatsContentDetailPreview() {
    KoinPreview {
        StatsContentDetail(
            StatsState.Ready(
                method = MeasureMethod.WEIGHT_ONLY,
                userSettings = UserSettings.Samples.simpleData,
                measures = Measure.Samples.simpleData,
                showMethod = false
            )
        ) {}
    }
}

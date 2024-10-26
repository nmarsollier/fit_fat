package com.nmarsollier.fitfat.ui.measures.edit

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.nmarsollier.fitfat.R
import com.nmarsollier.fitfat.models.measures.*
import com.nmarsollier.fitfat.models.measures.db.*
import com.nmarsollier.fitfat.models.userSettings.*
import com.nmarsollier.fitfat.ui.common.dialogs.*
import com.nmarsollier.fitfat.ui.common.preview.*
import com.nmarsollier.fitfat.ui.common.viewModel.reduceWith
import com.nmarsollier.fitfat.ui.measures.*
import com.nmarsollier.fitfat.ui.userSettings.*
import com.nmarsollier.fitfat.utils.*

@Composable
fun EditMeasureDetails(
    userSettings: UserSettings, measure: Measure, reducer: (EditMeasureAction) -> Unit
) {
    val context = LocalContext.current

    Column(
        Modifier
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.surface)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Row(modifier = Modifier
            .padding(top = 12.dp, bottom = 12.dp)
            .clickable {
                EditMeasureAction.ToggleMeasureMethod.reduceWith(reducer)
            }) {
            Text(stringResource(measure.measureMethod.labelRes))

            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Delete",
                tint = MaterialTheme.colorScheme.secondaryContainer
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 24.dp)
        ) {
            TextField(
                value = measure.date.formatDate,
                onValueChange = {},
                readOnly = true,
                enabled = false,
                label = { Text(stringResource(R.string.new_measure_date)) },
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .clickable {
                        showDatePicker(context, measure.date) {
                            EditMeasureAction.UpdateDate(it).reduceWith(reducer)
                        }
                    },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                )
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
        val measures = MeasureValue.entries.filter { it.isRequiredForMethod(method) }
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
private fun EditMeasureDetailsPreview() {
    KoinPreview {
        Column {
            EditMeasureDetails(
                UserSettings.Samples.simpleData,
                Measure.Samples.simpleData[0],
            ) {}
        }
    }
}

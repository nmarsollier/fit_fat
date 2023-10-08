package com.nmarsollier.fitfat.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.nmarsollier.fitfat.model.measures.MeasureMethod
import com.nmarsollier.fitfat.utils.KoinPreview

@Composable
fun MeasureMethodDialog(
    measureMethod: MeasureMethod,
    onChange: (MeasureMethod?) -> Unit
) {
    val context = LocalContext.current

    val measureMethods =
        MeasureMethod.values().map { it to context.getString(it.labelRes) }.toTypedArray()
    val selected = measureMethod.ordinal

    Dialog(onDismissRequest = { onChange(null) }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                Modifier.padding(10.dp)
            ) {
                measureMethods.forEach {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .background(color = if (selected == it.first.ordinal) AppColors.primary else AppColors.background)
                            .padding(4.dp)
                            .clickable {
                                onChange(it.first)
                            }
                    ) {
                        Text(text = it.second)
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun MeasureMethodPreview() {
    KoinPreview {
        MeasureMethodDialog(
            MeasureMethod.JACKSON_POLLOCK_7
        ) {}
    }
}
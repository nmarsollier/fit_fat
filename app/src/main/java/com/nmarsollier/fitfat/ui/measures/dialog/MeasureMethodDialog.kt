package com.nmarsollier.fitfat.ui.measures.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.nmarsollier.fitfat.models.measures.db.MeasureMethod
import com.nmarsollier.fitfat.ui.common.preview.KoinPreview
import com.nmarsollier.fitfat.ui.measures.labelRes

@Composable
fun MeasureMethodDialog(
    measureMethod: MeasureMethod,
    onChange: (MeasureMethod?) -> Unit
) {
    val context = LocalContext.current

    val measureMethods = MeasureMethod.entries
        .map { it to context.getString(it.labelRes) }
        .toTypedArray()

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
                            .background(color = if (selected == it.first.ordinal) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background)
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
private fun MeasureMethodPreview() {
    KoinPreview {
        MeasureMethodDialog(
            MeasureMethod.JACKSON_POLLOCK_7
        ) {}
    }
}
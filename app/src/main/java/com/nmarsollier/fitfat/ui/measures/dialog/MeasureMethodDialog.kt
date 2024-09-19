package com.nmarsollier.fitfat.ui.measures.dialog

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.*
import com.nmarsollier.fitfat.models.measures.db.*
import com.nmarsollier.fitfat.ui.common.preview.*
import com.nmarsollier.fitfat.ui.measures.*

@Composable
fun MeasureMethodDialog(
    measureMethod: MeasureMethod, onChange: (MeasureMethod?) -> Unit
) {
    val context = LocalContext.current

    val measureMethods =
        MeasureMethod.entries.map { it to context.getString(it.labelRes) }.toTypedArray()

    val selected = measureMethod.ordinal

    Dialog(onDismissRequest = { onChange(null) }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp)
            ) {
                measureMethods.forEach {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .background(color = if (selected == it.first.ordinal) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                            .padding(4.dp)
                            .clickable {
                                onChange(it.first)
                            }) {
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
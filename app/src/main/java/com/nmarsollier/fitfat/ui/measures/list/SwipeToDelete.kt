package com.nmarsollier.fitfat.ui.measures.list

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.unit.*
import com.nmarsollier.fitfat.models.measures.*
import com.nmarsollier.fitfat.models.userSettings.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SwipeToDelete(
    measure: Measure, userSettings: UserSettings, reduce: (MeasuresListAction) -> Unit
) {
    var offset by remember { mutableFloatStateOf(0f) }

    Box(modifier = Modifier
        .background(MaterialTheme.colorScheme.onError)
        .fillMaxWidth()
        .pointerInput(Unit) {
            detectHorizontalDragGestures { change, dragAmount ->
                offset += dragAmount
                change.consume()

                offset = kotlin.math.max(-80.dp.toPx(), kotlin.math.min(0f, offset))
            }
        }) {
        Box(
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            IconButton(modifier = Modifier.padding(horizontal = 16.dp), onClick = {
                reduce(MeasuresListAction.DeleteMeasure(measure = measure))
            }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        MeasureItemView(
            Modifier.offset { IntOffset(offset.toInt(), 0) }, userSettings, measure, reduce
        )
    }
}


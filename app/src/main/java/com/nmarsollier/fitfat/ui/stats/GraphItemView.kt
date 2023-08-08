@file:OptIn(ExperimentalFoundationApi::class)

package com.nmarsollier.fitfat.ui.stats

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.viewinterop.*
import androidx.core.content.*
import com.nmarsollier.fitfat.models.measures.*
import com.nmarsollier.fitfat.models.measures.db.*
import com.nmarsollier.fitfat.models.userSettings.*
import com.nmarsollier.fitfat.ui.common.preview.*
import com.nmarsollier.fitfat.ui.measures.*
import com.nmarsollier.fitfat.ui.userSettings.*
import com.nmarsollier.fitfat.utils.*
import lecho.lib.hellocharts.formatter.*
import lecho.lib.hellocharts.model.*
import lecho.lib.hellocharts.view.*
import kotlin.math.*

@Composable
@ExperimentalFoundationApi
fun GraphItemView(
    userSettings: UserSettings,
    measure: MeasureValue,
    graphValues: List<Measure>
) {
    val color = MaterialTheme.colorScheme.primary
    AndroidView(
        modifier = Modifier
            .padding(16.dp)
            .height(150.dp)
            .fillMaxWidth(),
        factory = { context ->
            LineChartView(context)
        },
        update = { view ->
            updateGraph(view, graphValues, measure, userSettings, color)
        }
    )
}

private const val A_DAY = (1000 * 60 * 60 * 24)

private fun updateGraph(
    view: LineChartView,
    values: List<Measure>,
    measure: MeasureValue,
    userSettings: UserSettings,
    color: Color
) {
    val context = view.context ?: return

    // Lines with values
    val lines = mutableListOf<Line>()
    val axisValues = mutableListOf<AxisValue>()

    var minDate = Long.MAX_VALUE
    var maxDate = 0L

    var minScaleY = Double.MAX_VALUE
    var maxScaleY = 0.0

    val lineValues = mutableListOf<PointValue>()

    values
        .filter {
            it.displayValue(measure, userSettings) > 0.0
        }
        // Map Pair the date part of the day as long, to the value measured
        .map {
            Pair(
                it.date.truncateTime,
                it.displayValue(measure, userSettings)
            )
        }
        // Group by date
        .groupBy { it.first }
        // Average per date, all values grouped
        .map {
            Pair(
                it.key,
                (it.value.sumOf { p -> p.second }) / it.value.size
            )
        }

        // For each date->average value add lines
        .forEach { pair ->
            minDate = min(pair.first, minDate)
            maxDate = max(pair.first, maxDate)

            minScaleY = min(pair.second, minScaleY)
            maxScaleY = max(pair.second, maxScaleY)

            lineValues.add(PointValue(pair.first.toFloat(), pair.second.toFloat()))
            axisValues.add(AxisValue(pair.first.toFloat()).apply {
                setLabel(java.util.Date(pair.first).formatShortDate)
            })
        }

    lines.add(
        Line(lineValues).also {
            it.color = ContextCompat.getColor(context, measure.colorRes)
            it.setHasPoints(true)
            it.strokeWidth = 4
            it.setHasLabels(true)
            it.setHasLabelsOnlyForSelected(true)
        }
    )

    val data = LineChartData(lines)

    val yAxisRange = IntRange(
        (minScaleY * 0.9).toInt(),
        (maxScaleY * 1.1).toInt()
    ).map { AxisValue(it.toFloat()).setLabel(it.toString()) }.toList()

    data.axisYLeft = Axis(yAxisRange).apply {
        setHasLines(true).maxLabelChars = 4
        textColor = color.hashCode()
    }

    // X labels at the bottom
    data.axisXBottom = Axis(axisValues).apply {
        textColor = color.hashCode()
        maxLabelChars = 8
        formatter = SimpleAxisValueFormatter()
        setHasLines(true)
        setHasTiltedLabels(true)
    }

    // When graph is only one date, dots are not displayed properly
    val vp =
        Viewport(
            minDate.toFloat() - A_DAY,
            (maxScaleY * 1.1).toFloat(),
            maxDate.toFloat() + A_DAY,
            (minScaleY * 0.9).toFloat()
        )
    view.maximumViewport = vp
    view.currentViewport = vp
    view.isViewportCalculationEnabled = false

    view.lineChartData = data
}


@Preview(showSystemUi = true)
@Composable
private fun GraphItemViewPreview() {
    KoinPreview {
        GraphItemView(
            userSettings = UserSettings.Samples.simpleData,
            measure = MeasureValue.BODY_WEIGHT,
            graphValues = Measure.Samples.simpleData
        )
    }
}


@file:OptIn(ExperimentalFoundationApi::class)

package com.nmarsollier.fitfat.stats.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.nmarsollier.fitfat.common.converters.formatShortDate
import com.nmarsollier.fitfat.common.converters.truncateTime
import com.nmarsollier.fitfat.common.ui.preview.KoinPreview
import com.nmarsollier.fitfat.common.ui.theme.AppColors
import com.nmarsollier.fitfat.measures.model.Measure
import com.nmarsollier.fitfat.measures.model.db.MeasureData
import com.nmarsollier.fitfat.measures.model.db.MeasureValue
import com.nmarsollier.fitfat.measures.samples.Samples
import com.nmarsollier.fitfat.measures.ui.colorRes
import com.nmarsollier.fitfat.measures.ui.displayValue
import com.nmarsollier.fitfat.userSettings.model.UserSettings
import com.nmarsollier.fitfat.userSettings.samples.Samples
import lecho.lib.hellocharts.formatter.SimpleAxisValueFormatter
import lecho.lib.hellocharts.model.Axis
import lecho.lib.hellocharts.model.AxisValue
import lecho.lib.hellocharts.model.Line
import lecho.lib.hellocharts.model.LineChartData
import lecho.lib.hellocharts.model.PointValue
import lecho.lib.hellocharts.model.Viewport
import lecho.lib.hellocharts.view.LineChartView
import kotlin.math.max
import kotlin.math.min

@Composable
@ExperimentalFoundationApi
fun GraphItemView(
    userSettings: com.nmarsollier.fitfat.userSettings.model.db.UserSettingsData,
    measure: MeasureValue,
    graphValues: List<MeasureData>
) {
    AndroidView(
        modifier = Modifier
            .padding(16.dp)
            .height(150.dp)
            .fillMaxWidth(),
        factory = { context ->
            LineChartView(context)
        },
        update = { view ->
            updateGraph(view, graphValues, measure, userSettings)
        }
    )
}

private const val A_DAY = (1000 * 60 * 60 * 24)

private fun updateGraph(
    view: LineChartView,
    values: List<MeasureData>,
    measure: MeasureValue,
    userSettings: com.nmarsollier.fitfat.userSettings.model.db.UserSettingsData
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
                it.date.truncateTime(),
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
                setLabel(java.util.Date(pair.first).formatShortDate())
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
        textColor = AppColors.primary.hashCode()
    }

    // X labels at the bottom
    data.axisXBottom = Axis(axisValues).apply {
        textColor = AppColors.primary.hashCode()
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
            UserSettings.Samples.simpleData.value,
            measure = MeasureValue.BODY_WEIGHT,
            graphValues = Measure.Samples.simpleData.map { it.value }
        )
    }
}


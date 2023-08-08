@file:OptIn(ExperimentalFoundationApi::class)

package com.nmarsollier.fitfat.ui.stats

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
import com.nmarsollier.fitfat.R
import com.nmarsollier.fitfat.model.measures.Measure
import com.nmarsollier.fitfat.model.measures.MeasureValue
import com.nmarsollier.fitfat.model.userSettings.UserSettingsEntity
import com.nmarsollier.fitfat.ui.common.colorRes
import com.nmarsollier.fitfat.utils.KoinPreview
import com.nmarsollier.fitfat.utils.Samples
import com.nmarsollier.fitfat.utils.formatShortDate
import com.nmarsollier.fitfat.utils.truncateTime
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
fun GraphItemView(userSettingsEntity: UserSettingsEntity, measure: MeasureValue, graphValues: List<Measure>) {
    AndroidView(
        modifier = Modifier
            .padding(16.dp)
            .height(150.dp)
            .fillMaxWidth(),
        factory = { context ->
            LineChartView(context)
        },
        update = { view ->
            updateGraph(view, graphValues, measure, userSettingsEntity)
        }
    )
}

private const val A_DAY = (1000 * 60 * 60 * 24)

private fun updateGraph(
    view: LineChartView,
    values: List<Measure>,
    measure: MeasureValue,
    userSettingsEntity: UserSettingsEntity
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
            it.getValueForMethod(measure, userSettingsEntity).toDouble() > 0.0
        }
        // Map Pair the date part of the day as long, to the value measured
        .map {
            Pair(
                it.date.truncateTime(),
                it.getValueForMethod(measure, userSettingsEntity).toDouble()
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
        textColor = ContextCompat.getColor(context, R.color.colorPrimary)
    }

    // X labels at the bottom
    data.axisXBottom = Axis(axisValues).apply {
        textColor = ContextCompat.getColor(context, R.color.colorPrimary)
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
fun GraphItemViewPreview() {
    KoinPreview {
        GraphItemView(
            UserSettingsEntity.Samples.simpleData,
            measure = MeasureValue.BODY_WEIGHT,
            graphValues = Measure.Samples.simpleData
        )
    }
}


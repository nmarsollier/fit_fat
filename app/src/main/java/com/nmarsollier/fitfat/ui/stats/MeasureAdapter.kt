package com.nmarsollier.fitfat.ui.stats

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.style.DynamicDrawableSpan
import android.text.style.ImageSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import com.nmarsollier.fitfat.R
import com.nmarsollier.fitfat.databinding.MainStatsMeasureGraphHolderBinding
import com.nmarsollier.fitfat.model.Measure
import com.nmarsollier.fitfat.model.MeasureValue
import com.nmarsollier.fitfat.model.UserSettings
import com.nmarsollier.fitfat.utils.dpToPx
import com.nmarsollier.fitfat.utils.formatShortDate
import com.nmarsollier.fitfat.utils.joinToSpannedString
import com.nmarsollier.fitfat.utils.truncateTime
import kotlinx.android.extensions.LayoutContainer
import lecho.lib.hellocharts.formatter.SimpleAxisValueFormatter
import lecho.lib.hellocharts.model.*
import kotlin.math.max
import kotlin.math.min

private const val A_DAY = (1000 * 60 * 60 * 24)

class MeasureAdapter(
    private val context: Context,
    private val userSettings: UserSettings,
    private val measures: List<MeasureValue>,
    private val graphValues: List<Measure>?
) :
    RecyclerView.Adapter<MeasureGraphHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeasureGraphHolder {
        return MeasureGraphHolder.newInstance(parent, context)
    }

    override fun onBindViewHolder(holder: MeasureGraphHolder, position: Int) {
        holder.bind(userSettings, measures[position], graphValues)
    }

    override fun getItemCount() = measures.size
}

class MeasureGraphHolder(private val binding: MainStatsMeasureGraphHolderBinding) :
    RecyclerView.ViewHolder(binding.root),
    LayoutContainer {
    private var graphValues: List<Measure>? = null

    private var userSettings: UserSettings? = null
    private var measure: MeasureValue? = null

    fun bind(
        userSettings: UserSettings,
        measure: MeasureValue,
        graphValues: List<Measure>?
    ) {
        if (this.measure != measure) {
            this.userSettings = userSettings
            this.measure = measure
            this.graphValues = graphValues
            updateGraph()
        }
    }

    private fun updateGraph() {
        val context = itemView.context ?: return
        val values = graphValues ?: return
        val measure = this.measure ?: return

        // Lines with values
        val legends = mutableListOf<MeasureValue>()
        val lines = mutableListOf<Line>()
        val axisValues = mutableListOf<AxisValue>()

        var minDate = Long.MAX_VALUE
        var maxDate = 0L

        var minScaleY = Double.MAX_VALUE
        var maxScaleY = 0.0

        val lineValues = mutableListOf<PointValue>()

        values
            .filter {
                it.getValueForMethod(measure, userSettings).toDouble() > 0.0
            }
            // Map Pair the date part of the day as long, to the value measured
            .map {
                Pair(
                    it.date.truncateTime(),
                    it.getValueForMethod(measure, userSettings).toDouble()
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

        legends.add(measure)

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
        binding.vChart.maximumViewport = vp
        binding.vChart.currentViewport = vp
        binding.vChart.isViewportCalculationEnabled = false

        binding.vChart.lineChartData = data

        setLegends(legends)
    }


    private fun setLegends(legends: List<MeasureValue>) {
        val context = itemView.context ?: return

        val bulletSize = context.dpToPx(14).toInt()

        binding.vLegend.text = legends.joinToSpannedString(separator = "     ") {
            SpannableString("\u00A0\u00A0" + context.getString(it.titleRes)).also { s ->

                val bulletIcon = DrawableCompat.wrap(
                    ContextCompat.getDrawable(context, R.drawable.ic_dot_24dp)!!.mutate()
                )
                bulletIcon.setBounds(0, 0, bulletSize, bulletSize)
                DrawableCompat.setTint(bulletIcon, ContextCompat.getColor(context, it.colorRes))

                s.setSpan(
                    ImageSpan(bulletIcon, DynamicDrawableSpan.ALIGN_BASELINE),
                    0,
                    1,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }
    }

    override val containerView: View
        get() = itemView

    val context: Context = binding.root.context

    companion object {
        fun newInstance(parent: ViewGroup, context: Context): MeasureGraphHolder {
            return MeasureGraphHolder(
                MainStatsMeasureGraphHolderBinding.inflate(
                    LayoutInflater.from(context),
                    parent,
                    false
                )
            )
        }
    }
}
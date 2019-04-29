package com.nmarsollier.fitfat

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.DynamicDrawableSpan
import android.text.style.ImageSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.evernote.android.state.State
import com.nmarsollier.fitfat.model.Measure
import com.nmarsollier.fitfat.model.MeasureMethod
import com.nmarsollier.fitfat.model.MeasureValue
import com.nmarsollier.fitfat.model.getRoomDatabase
import com.nmarsollier.fitfat.utils.dpToPx
import com.nmarsollier.fitfat.utils.formatShortDate
import com.nmarsollier.fitfat.utils.joinToSpannedString
import com.nmarsollier.fitfat.utils.truncateTime
import kotlinx.android.synthetic.main.main_stats_fragment.*
import lecho.lib.hellocharts.formatter.SimpleAxisValueFormatter
import lecho.lib.hellocharts.model.*
import java.util.*


private const val A_DAY = (1000 * 60 * 60 * 24)

class MainStats : LifecycleOwner, Fragment() {

    @State
    var selectedMethod = MeasureMethod.WEIGHT_ONLY

    private var graphValues: List<Measure>? = null

    private val maxScaleY
        get() =
            MeasureValue.values().filter { it.isRequired(selectedMethod) }.maxBy { it.maxScale }?.maxScale ?: 100

    private val yAxisRange
        get() = IntRange(0, maxScaleY).map { AxisValue(it.toFloat()).setLabel(it.toString()) }.toList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.main_stats_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vMeasureMethod.setOnClickListener { showMeasureTypeSelectionDialog() }
        getRoomDatabase(context!!).measureDao().getMeasures()
            .observe(this, Observer<List<Measure>> { measures ->
                measures?.let { values ->
                    graphValues = values
                    updateGraph()
                }
            })
        refreshUI()
    }

    private fun showMeasureTypeSelectionDialog() {
        val context = context ?: return

        val measureMethods = MeasureMethod.values().map { getString(it.labelRes) }.toTypedArray()

        var dialog: AlertDialog? = null
        dialog = AlertDialog.Builder(context)
            .setTitle(R.string.new_measure_method_title)
            .setSingleChoiceItems(measureMethods, selectedMethod.ordinal) { _, which ->
                setSelectedMeasureType(MeasureMethod.values()[which])
                dialog?.dismiss()
            }
            .setNegativeButton(android.R.string.cancel, null)
            .create()
        dialog.show()
    }

    private fun setSelectedMeasureType(measureMethod: MeasureMethod) {
        getRoomDatabase(context!!).measureDao().getMeasures().removeObservers(this)
        selectedMethod = measureMethod
        updateGraph()
        refreshUI()
    }

    private fun refreshUI() {
        vMeasureMethod.setText(selectedMethod.labelRes)
    }

    private fun updateGraph() {
        val context = context ?: return
        val values = graphValues ?: return

        // Lines with values
        val legends = mutableListOf<MeasureValue>()
        val lines = mutableListOf<Line>()
        val axisValues = mutableListOf<AxisValue>()

        var minDate = Long.MAX_VALUE
        var maxDate = 0L

        MeasureValue.values()
            .filter {
                if (selectedMethod == MeasureMethod.WEIGHT_ONLY) {
                    it.isRequired(selectedMethod)
                } else {
                    it.isRequired(selectedMethod) || it == MeasureValue.BODY_FAT
                }
            }
            .forEach { method ->
                val lineValues = mutableListOf<PointValue>()

                values
                    // First filter values that are not representative
                    .filter {
                        (selectedMethod == MeasureMethod.WEIGHT_ONLY || selectedMethod == it.measureMethod)
                                && it.getValueForMethod(method).toInt() > 0
                    }
                    // Map Pair the date part of the day as long, to the value measured
                    .map { Pair(it.date.truncateTime(), it.getValueForMethod(method).toFloat()) }
                    // Group by date
                    .groupBy { it.first }
                    // Average per date, all values grouped
                    .map { Pair(it.key, (it.value.sumByDouble { p -> p.second.toDouble() }).toFloat() / it.value.size) }
                    // For each date->average value add lines
                    .forEach { pair ->
                        minDate = Math.min(pair.first, minDate)
                        maxDate = Math.max(pair.first, maxDate)
                        lineValues.add(PointValue(pair.first.toFloat(), pair.second))
                        axisValues.add(AxisValue(pair.first.toFloat()).apply {
                            setLabel(Date(pair.first).formatShortDate())
                        })
                    }

                lines.add(
                    Line(lineValues).also {
                        it.color = ContextCompat.getColor(context, method.colorRes)
                        it.setHasPoints(true)
                        it.strokeWidth = 4
                        it.setHasLabels(true)
                        it.setHasLabelsOnlyForSelected(true)
                    }
                )

                legends.add(method)
            }

        val data = LineChartData(lines)
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

        if (minDate == maxDate) {
            val vp = Viewport(minDate.toFloat() - A_DAY, maxScaleY.toFloat(), maxDate.toFloat() + A_DAY, 0f)
            vChart.maximumViewport = vp
            vChart.currentViewport = vp
            vChart.isViewportCalculationEnabled = false
        } else {
            vChart.isViewportCalculationEnabled = true
            vChart.resetViewports()
        }

        vChart.lineChartData = data

        setLegends(legends)
    }

    private fun setLegends(legends: List<MeasureValue>) {
        val context = context ?: return

        val bulletSize = context.dpToPx(14).toInt()

        vLegend.text = legends.joinToSpannedString(separator = "     ") {
            SpannableString("\u00A0\u00A0" + getString(it.titleRes)).also { s ->

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

    companion object {
        fun newInstance() = MainStats()
    }
}

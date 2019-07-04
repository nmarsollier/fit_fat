package com.nmarsollier.fitfat

import android.content.Context
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
import androidx.recyclerview.widget.RecyclerView
import com.evernote.android.state.State
import com.nmarsollier.fitfat.model.*
import com.nmarsollier.fitfat.utils.*
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.main_stats_fragment.*
import kotlinx.android.synthetic.main.main_stats_measure_graph_holder.*
import lecho.lib.hellocharts.formatter.SimpleAxisValueFormatter
import lecho.lib.hellocharts.model.*
import kotlin.math.max
import kotlin.math.min

private const val A_DAY = (1000 * 60 * 60 * 24)

class MainStats : LifecycleOwner, Fragment() {

    @State
    var selectedMethod = MeasureMethod.WEIGHT_ONLY

    private var userSettings: UserSettings? = null

    private var graphValues: List<Measure>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.main_stats_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vMeasureMethod.setOnClickListener { showMeasureTypeSelectionDialog() }

        loadSettings()

        getRoomDatabase(context!!).measureDao().findAll()
            .observe(this, Observer<List<Measure>> { measures ->
                measures?.let { values ->
                    graphValues = values
                    initAdapter()
                }
            })

        refreshUI()
    }

    private fun loadSettings() {
        val context = context ?: return
        runInBackground {
            userSettings = getRoomDatabase(context).userDao().getUserSettings()

            runInForeground {
                initAdapter()
            }
        }
    }

    private fun initAdapter() {
        val context = context ?: return
        val settings = userSettings ?: return

        val values = MeasureValue.values()
            .filter {
                if (selectedMethod == MeasureMethod.WEIGHT_ONLY) {
                    it.isRequired(selectedMethod)
                } else {
                    it.isRequired(selectedMethod) || it == MeasureValue.BODY_FAT
                }
            }

        vRecyclerView.adapter = MeasureAdapter(context, settings, values, graphValues)
    }

    private fun showMeasureTypeSelectionDialog() {
        val context = context ?: return

        val measureMethods = MeasureMethod.values().map { getString(it.labelRes) }.toTypedArray()

        AlertDialog.Builder(context)
            .setTitle(R.string.new_measure_method_title)
            .setSingleChoiceItems(measureMethods, selectedMethod.ordinal) { dialog, which ->
                setSelectedMeasureType(MeasureMethod.values()[which])
                dialog.dismiss()
            }
            .setNegativeButton(android.R.string.cancel, null)
            .create().also {
                it.show()
            }
    }

    private fun setSelectedMeasureType(measureMethod: MeasureMethod) {
        getRoomDatabase(context!!).measureDao().findAll().removeObservers(this)
        selectedMethod = measureMethod
        initAdapter()
        refreshUI()
    }

    private fun refreshUI() {
        vMeasureMethod.setText(selectedMethod.labelRes)
    }

    companion object {
        fun newInstance() = MainStats()
    }

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

    class MeasureGraphHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView), LayoutContainer {
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
                .map { Pair(it.date.truncateTime(), it.getValueForMethod(measure, userSettings).toDouble()) }
                // Group by date
                .groupBy { it.first }
                // Average per date, all values grouped
                .map {
                    Pair(
                        it.key,
                        (it.value.sumByDouble { p -> p.second }) / it.value.size
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
            vChart.maximumViewport = vp
            vChart.currentViewport = vp
            vChart.isViewportCalculationEnabled = false

            vChart.lineChartData = data

            setLegends(legends)
        }


        private fun setLegends(legends: List<MeasureValue>) {
            val context = itemView.context ?: return

            val bulletSize = context.dpToPx(14).toInt()

            vLegend.text = legends.joinToSpannedString(separator = "     ") {
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

        override val containerView: View?
            get() = itemView

        val context: Context = itemView.context


        companion object {
            fun newInstance(parent: ViewGroup, context: Context): MeasureGraphHolder {
                return MeasureGraphHolder(
                    LayoutInflater.from(context).inflate(
                        R.layout.main_stats_measure_graph_holder,
                        parent,
                        false
                    )
                )
            }
        }
    }
}

package com.nmarsollier.fitfat.components

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.nmarsollier.fitfat.R
import com.nmarsollier.fitfat.model.*
import com.nmarsollier.fitfat.utils.formatString
import com.nmarsollier.fitfat.utils.onProgressChangeListener
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.help_dialog.*
import kotlinx.android.synthetic.main.new_measure_double_holder.*
import kotlinx.android.synthetic.main.new_measure_int_holder.*


class MeasuresAdapter internal constructor(
    private val context: Context,
    private var measure: Measure,
    var userSettings: UserSettings?,
    private val readOnly: Boolean,
    private var callback: () -> Unit
) : RecyclerView.Adapter<MeasureHolder>() {

    private var measures = mutableListOf<MeasureValue>()

    fun setData(measure: Measure) {
        this.measure = measure
        val method = measure.measureMethod
        measures = MeasureValue.values().filter { it.isRequired(method) }.toMutableList()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeasureHolder {
        return when (InputType.values()[viewType]) {
            InputType.DOUBLE -> newMeasureDoubleHolder(parent, context)
            InputType.INT -> newMeasureIntHolder(parent, context)
        }
    }

    override fun onBindViewHolder(holder: MeasureHolder, position: Int) {
        val userSettings = userSettings ?: return
        holder.bind(measures[position], measure, userSettings, callback, readOnly)
    }

    override fun getItemCount(): Int {
        return measures.size
    }

    override fun getItemViewType(position: Int): Int {
        return measures[position].inputType.ordinal
    }

    private fun newMeasureIntHolder(parent: ViewGroup, context: Context): MeasureHolder {
        return MeasureValueHolder(
            LayoutInflater.from(context).inflate(
                R.layout.new_measure_int_holder,
                parent,
                false
            )
        )
    }

    private fun newMeasureDoubleHolder(parent: ViewGroup, context: Context): MeasureHolder {
        return DoubleHolder(
            LayoutInflater.from(context).inflate(
                R.layout.new_measure_double_holder,
                parent,
                false
            )
        )
    }
}

abstract class MeasureHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView), LayoutContainer {
    override val containerView: View?
        get() = itemView

    val context: Context = itemView.context

    abstract fun bind(
        measureValue: MeasureValue,
        measure: Measure,
        userSettings: UserSettings,
        callback: () -> Unit,
        readOnly: Boolean
    )
}

class MeasureValueHolder constructor(itemView: View) : MeasureHolder(itemView) {
    private var measureValue: MeasureValue? = null
    private var measure: Measure? = null
    private lateinit var userSettings: UserSettings

    override fun bind(
        measureValue: MeasureValue,
        measure: Measure,
        userSettings: UserSettings,
        callback: () -> Unit,
        readOnly: Boolean
    ) {
        this.measureValue = measureValue
        this.measure = measure
        this.userSettings = userSettings

        vValueBar.max = measureValue.maxScale
        vValueUnit.setText(R.string.unit_mm)

        val value = getValue()
        vValueText.text = value.formatString()

        vValueBar.isVisible = !readOnly
        vHelpIcon.isVisible = !readOnly
        vTitleLabel.text = context.getString(measureValue.titleRes)

        if (!readOnly) {
            vValueBar.progress = value

            vValueBar.setOnSeekBarChangeListener(
                onProgressChangeListener { _, progress, _ ->
                    setValue(progress, measureValue)
                    vValueText.text = getValue().formatString()
                    callback.invoke()
                }
            )

            measureValue.helpRes?.let { helpRes ->
                vHelpIcon.isVisible = true
                vHelpIcon.setOnClickListener {
                    Dialog(context).apply {
                        setContentView(R.layout.help_dialog)
                        vHelpView.setOnClickListener {
                            dismiss()
                        }
                        vHelpPicture.setImageResource(helpRes)
                        show()
                    }
                }
            } ?: run {
                vHelpIcon.isVisible = false
            }
        }
    }

    fun setValue(value: Int, fromMeasureValue: MeasureValue) {
        val measureValue = measureValue
        if (fromMeasureValue != measureValue) {
            return
        }

        measure?.setValueForMethod(measureValue, value)
    }

    fun getValue(): Int {
        val measureValue = measureValue ?: return 0
        val measure = measure ?: return 0
        return measure.getValueForMethod(measureValue).toInt()
    }
}

class DoubleHolder constructor(itemView: View) : MeasureHolder(itemView) {
    private var measureValue: MeasureValue? = null
    private var measure: Measure? = null
    private lateinit var userSettings: UserSettings

    override fun bind(
        measureValue: MeasureValue,
        measure: Measure,
        userSettings: UserSettings,
        callback: () -> Unit,
        readOnly: Boolean
    ) {
        this.measureValue = measureValue
        this.measure = measure
        this.userSettings = userSettings

        vIntBar.isVisible = !readOnly
        vDecimalBar.isVisible = !readOnly
        vFatTextLabel.text = context.getString(measureValue.titleRes)
        vMeasureUnit.text = when (measureValue.unitType) {
            UnitType.PERCENT -> context.getString(R.string.unit_percent)
            UnitType.WEIGHT -> context.getString(userSettings.measureSystem.weightResId)
            UnitType.WIDTH -> context.getString(R.string.unit_mm)
        }

        if (!readOnly) {
            if (measureValue.unitType == UnitType.WEIGHT) {
                vIntBar.max =
                    userSettings.measureSystem.displayWeight(measureValue.maxScale.toDouble()).toInt()
            } else {
                vIntBar.max = measureValue.maxScale
            }

            val currentValue = getCurrentValue()

            vIntBar.progress = currentValue.toInt()
            vDecimalBar.progress = ((currentValue - currentValue.toInt()) * 10).toInt()

            vIntBar.setOnSeekBarChangeListener(
                onProgressChangeListener { _, progress, _ ->
                    setIntValue(progress, measureValue, callback)
                }
            )

            vDecimalBar.setOnSeekBarChangeListener(
                onProgressChangeListener { _, progress, _ ->
                    setDecimalValue(progress, measureValue, callback)
                }
            )
        }

        updateUnits(callback)
    }

    private fun setIntValue(
        progress: Int,
        fromMeasureValue: MeasureValue,
        callback: () -> Unit
    ) {
        val measureValue = measureValue ?: return
        val measure = measure ?: return
        if (fromMeasureValue != measureValue) {
            return
        }

        val currentValue = getCurrentValue()
        val decimal = currentValue - currentValue.toInt()

        var newValue = progress.toDouble() + decimal
        if (measureValue.unitType == UnitType.WEIGHT) {
            newValue = userSettings.measureSystem.standardWeight(newValue)
        }

        measure.setValueForMethod(measureValue, newValue)
        updateUnits(callback)
    }

    private fun setDecimalValue(
        progress: Int,
        fromMeasureValue: MeasureValue,
        callback: () -> Unit
    ) {
        val measureValue = measureValue
        val measure = measure ?: return
        if (fromMeasureValue != measureValue) {
            return
        }

        val currentValue = measure.getValueForMethod(measureValue)
        var newValue = currentValue.toInt().toDouble() + (progress.toDouble() / 10)
        if (measureValue.unitType == UnitType.WEIGHT) {
            newValue = userSettings.measureSystem.standardWeight(newValue)
        }

        measure.setValueForMethod(measureValue, newValue)
        updateUnits(callback)
    }

    private fun getCurrentValue(): Double {
        val measureValue = measureValue ?: return 0.0
        val measure = measure ?: return 0.0

        var currentValue = measure.getValueForMethod(measureValue).toDouble()
        if (measureValue.unitType == UnitType.WEIGHT) {
            currentValue = userSettings.measureSystem.displayWeight(currentValue)
        }
        return currentValue
    }

    private fun updateUnits(callback: () -> Unit) {
        vFatValueText.text = getCurrentValue().formatString()
        callback.invoke()
    }
}
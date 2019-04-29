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
import kotlinx.android.synthetic.main.help_dialog.*
import kotlinx.android.synthetic.main.new_measure_double_holder.view.*
import kotlinx.android.synthetic.main.new_measure_int_holder.view.*


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

abstract class MeasureHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
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

        itemView.vValueBar.max = measureValue.maxScale
        itemView.vValueUnit.setText(R.string.unit_mm)

        val value = getValue()
        itemView.vValueText.text = value.formatString()

        itemView.vValueBar.isVisible = !readOnly
        itemView.vHelpIcon.isVisible = !readOnly
        itemView.vTitleLabel.text = itemView.context.getString(measureValue.titleRes)

        if (!readOnly) {
            itemView.vValueBar.progress = value

            itemView.vValueBar.setOnSeekBarChangeListener(
                onProgressChangeListener { _, progress, _ ->
                    setValue(progress, measureValue)
                    itemView.vValueText.text = getValue().formatString()
                    callback.invoke()
                }
            )

            measureValue.helpRes?.let { helpRes ->
                itemView.vHelpIcon.isVisible = true
                itemView.vHelpIcon.setOnClickListener {
                    Dialog(itemView.context).apply {
                        setContentView(R.layout.help_dialog)
                        vHelpView.setOnClickListener {
                            dismiss()
                        }
                        vHelpPicture.setImageResource(helpRes)
                        show()
                    }
                }
            } ?: run {
                itemView.vHelpIcon.isVisible = false
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

        itemView.vIntBar.isVisible = !readOnly
        itemView.vDecimalBar.isVisible = !readOnly
        itemView.vFatTextLabel.text = itemView.context.getString(measureValue.titleRes)
        itemView.vMeasureUnit.text = when (measureValue.unitType) {
            UnitType.PERCENT -> itemView.context.getString(R.string.unit_percent)
            UnitType.WEIGHT -> itemView.context.getString(userSettings.measureSystem.weightResId)
            UnitType.WIDTH -> itemView.context.getString(R.string.unit_mm)
        }

        if (!readOnly) {
            if (measureValue.unitType == UnitType.WEIGHT) {
                itemView.vIntBar.max =
                    userSettings.measureSystem.displayWeight(measureValue.maxScale.toDouble()).toInt()
            } else {
                itemView.vIntBar.max = measureValue.maxScale
            }

            val currentValue = getCurrentValue()

            itemView.vIntBar.progress = currentValue.toInt()
            itemView.vDecimalBar.progress = ((currentValue - currentValue.toInt()) * 100).toInt()

            itemView.vIntBar.setOnSeekBarChangeListener(
                onProgressChangeListener { _, progress, _ ->
                    setIntValue(progress, measureValue, callback)
                }
            )

            itemView.vDecimalBar.setOnSeekBarChangeListener(
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
        var newValue = currentValue.toInt().toDouble() + (progress.toDouble() / 100)
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
        itemView.vFatValueText.text = getCurrentValue().formatString()
        callback.invoke()
    }
}
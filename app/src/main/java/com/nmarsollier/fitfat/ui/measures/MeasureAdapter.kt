package com.nmarsollier.fitfat.ui.measures

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.nmarsollier.fitfat.R
import com.nmarsollier.fitfat.databinding.HelpDialogBinding
import com.nmarsollier.fitfat.databinding.NewMeasureDoubleHolderBinding
import com.nmarsollier.fitfat.databinding.NewMeasureIntHolderBinding
import com.nmarsollier.fitfat.model.measures.InputType
import com.nmarsollier.fitfat.model.measures.Measure
import com.nmarsollier.fitfat.model.measures.MeasureValue
import com.nmarsollier.fitfat.model.measures.UnitType
import com.nmarsollier.fitfat.model.userSettings.UserSettings
import com.nmarsollier.fitfat.utils.formatString
import kotlinx.android.extensions.LayoutContainer

class MeasuresAdapter internal constructor(
    private val context: Context,
    private var measure: Measure,
    var userSettings: UserSettings?,
    private val readOnly: Boolean,
    private var callback: (measureValue: MeasureValue, value: Number) -> Unit
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
            NewMeasureIntHolderBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    private fun newMeasureDoubleHolder(parent: ViewGroup, context: Context): MeasureHolder {
        return DoubleHolder(
            NewMeasureDoubleHolderBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }
}

abstract class MeasureHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView),
    LayoutContainer {
    override val containerView: View?
        get() = itemView

    val context: Context = itemView.context

    abstract fun bind(
        measureValue: MeasureValue,
        measure: Measure,
        userSettings: UserSettings,
        callback: (measureValue: MeasureValue, value: Number) -> Unit,
        readOnly: Boolean
    )
}

class MeasureValueHolder(val binding: NewMeasureIntHolderBinding) : MeasureHolder(binding.root) {
    private var measureValue: MeasureValue? = null
    private var measure: Measure? = null
    private lateinit var userSettings: UserSettings

    override fun bind(
        measureValue: MeasureValue,
        measure: Measure,
        userSettings: UserSettings,
        callback: (measureValue: MeasureValue, value: Number) -> Unit,
        readOnly: Boolean
    ) {
        this.measureValue = measureValue
        this.measure = measure
        this.userSettings = userSettings

        binding.vValueBar.max = measureValue.maxScale
        binding.vValueUnit.setText(R.string.unit_mm)

        val value = getValue()
        binding.vValueText.text = value.formatString()

        binding.vValueBar.isVisible = !readOnly
        binding.vHelpIcon.isVisible = !readOnly
        binding.vTitleLabel.text = context.getString(measureValue.titleRes)

        if (!readOnly) {
            binding.vValueBar.progress = value

            binding.vValueBar.setOnSeekBarChangeListener(
                onProgressChangeListener { _, progress, _ ->
                    setValue(progress, measureValue, callback)
                    binding.vValueText.text = getValue().formatString()
                }
            )

            measureValue.helpRes?.let { helpRes ->
                binding.vHelpIcon.isVisible = true
                binding.vHelpIcon.setOnClickListener {
                    val bind =
                        HelpDialogBinding.inflate(LayoutInflater.from(context), binding.root, false)
                    Dialog(context).apply {
                        setContentView(bind.root)
                        bind.vHelpView.setOnClickListener {
                            dismiss()
                        }
                        bind.vHelpPicture.setImageResource(helpRes)
                        show()
                    }
                }
            } ?: run {
                binding.vHelpIcon.isVisible = false
            }
        }
    }

    fun setValue(
        value: Int,
        fromMeasureValue: MeasureValue,
        callback: (measure: MeasureValue, value: Number) -> Unit
    ) {
        val measureValue = measureValue
        if (fromMeasureValue != measureValue) {
            return
        }

        callback.invoke(measureValue, value)
    }

    fun getValue(): Int {
        val measureValue = measureValue ?: return 0
        val measure = measure ?: return 0
        return measure.getValueForMethod(measureValue).toInt()
    }
}

class DoubleHolder(val binding: NewMeasureDoubleHolderBinding) : MeasureHolder(binding.root) {
    private var measureValue: MeasureValue? = null
    private var measure: Measure? = null
    private lateinit var userSettings: UserSettings

    override fun bind(
        measureValue: MeasureValue,
        measure: Measure,
        userSettings: UserSettings,
        callback: (measureValue: MeasureValue, value: Number) -> Unit,
        readOnly: Boolean
    ) {
        this.measureValue = measureValue
        this.measure = measure
        this.userSettings = userSettings

        binding.intBar.isVisible = !readOnly
        binding.decimalBar.isVisible = !readOnly
        binding.vFatTextLabel.text = context.getString(measureValue.titleRes)
        binding.vMeasureUnit.text = when (measureValue.unitType) {
            UnitType.PERCENT -> context.getString(R.string.unit_percent)
            UnitType.WEIGHT -> context.getString(userSettings.measureSystem.weightResId)
            UnitType.WIDTH -> context.getString(R.string.unit_mm)
        }

        if (!readOnly) {
            if (measureValue.unitType == UnitType.WEIGHT) {
                binding.intBar.max =
                    userSettings.measureSystem.displayWeight(measureValue.maxScale.toDouble())
                        .toInt()
            } else {
                binding.intBar.max = measureValue.maxScale
            }

            val currentValue = getCurrentValue()

            binding.intBar.progress = currentValue.toInt()
            binding.decimalBar.progress = ((currentValue - currentValue.toInt()) * 10).toInt()

            binding.intBar.setOnSeekBarChangeListener(
                onProgressChangeListener { _, progress, _ ->
                    setIntValue(progress, measureValue, callback)
                    binding.fatPercentText.text = getCurrentValue().formatString()
                }
            )

            binding.decimalBar.setOnSeekBarChangeListener(
                onProgressChangeListener { _, progress, _ ->
                    setDecimalValue(progress, measureValue, callback)
                    binding.fatPercentText.text = getCurrentValue().formatString()
                }
            )
        }

        binding.fatPercentText.text = getCurrentValue().formatString()
    }

    private fun setIntValue(
        progress: Int,
        fromMeasureValue: MeasureValue,
        callback: (measureValue: MeasureValue, value: Number) -> Unit
    ) {
        val measureValue = measureValue ?: return
        measure ?: return
        if (fromMeasureValue != measureValue) {
            return
        }

        val currentValue = getCurrentValue()
        val decimal = currentValue - currentValue.toInt()

        var newValue = progress.toDouble() + decimal
        if (measureValue.unitType == UnitType.WEIGHT) {
            newValue = userSettings.measureSystem.standardWeight(newValue)
        }

        callback(measureValue, newValue)
    }

    private fun setDecimalValue(
        progress: Int,
        fromMeasureValue: MeasureValue,
        callback: (measureValue: MeasureValue, value: Number) -> Unit
    ) {
        val measureValue = measureValue
        val measure = measure ?: return
        if (fromMeasureValue != measureValue) {
            return
        }

        val currentValue = measure.getValueForMethod(measureValue, userSettings)
        var newValue = currentValue.toInt().toDouble() + (progress.toDouble() / 10)
        if (measureValue.unitType == UnitType.WEIGHT) {
            newValue = userSettings.measureSystem.standardWeight(newValue)
        }

        callback(measureValue, newValue)
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
}
package com.nmarsollier.fitfat.ui.editMeasure.ui

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.nmarsollier.fitfat.R
import com.nmarsollier.fitfat.common.uiUtils.displayValue
import com.nmarsollier.fitfat.common.uiUtils.displayWeight
import com.nmarsollier.fitfat.common.uiUtils.helpRes
import com.nmarsollier.fitfat.common.uiUtils.weightResId
import com.nmarsollier.fitfat.common.utils.formatString
import com.nmarsollier.fitfat.databinding.HelpDialogBinding
import com.nmarsollier.fitfat.databinding.NewMeasureDoubleHolderBinding
import com.nmarsollier.fitfat.databinding.NewMeasureIntHolderBinding
import com.nmarsollier.fitfat.models.measures.Measure
import com.nmarsollier.fitfat.models.measures.db.MeasureValue
import com.nmarsollier.fitfat.models.measures.isRequiredForMethod
import com.nmarsollier.fitfat.models.userSettings.UserSettings
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
        measures = MeasureValue.values().filter { it.isRequiredForMethod(method) }.toMutableList()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeasureHolder {
        return when (MeasureValue.InputType.values()[viewType]) {
            MeasureValue.InputType.DOUBLE -> newMeasureDoubleHolder(parent, context)
            MeasureValue.InputType.INT -> newMeasureIntHolder(parent, context)
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
                LayoutInflater.from(context), parent, false
            )
        )
    }

    private fun newMeasureDoubleHolder(parent: ViewGroup, context: Context): MeasureHolder {
        return DoubleHolder(
            NewMeasureDoubleHolderBinding.inflate(
                LayoutInflater.from(context), parent, false
            )
        )
    }
}

abstract class MeasureHolder(itemView: View) : RecyclerView.ViewHolder(itemView), LayoutContainer {
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

        var value = getValue()
        binding.vValueText.text = value.formatString()

        binding.vValueBar.isVisible = !readOnly
        binding.vHelpIcon.isVisible = !readOnly
        binding.vTitleLabel.text = context.getString(measureValue.titleRes)

        if (!readOnly) {
            binding.vValueBar.progress = value

            binding.vValueBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?, progress: Int, fromUser: Boolean
                ) {
                    value = progress
                    binding.vValueText.text = value.formatString()
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    // Code to execute when the user starts dragging the SeekBar
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    setValue(seekBar?.progress ?: 0, measureValue, callback)
                }
            })

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
        return measure.displayValue(measureValue, userSettings).toInt()
    }
}

class DoubleHolder(val binding: NewMeasureDoubleHolderBinding) : MeasureHolder(binding.root) {
    private var measureValue: MeasureValue? = null
    private var measure: Measure? = null
    private lateinit var userSettings: UserSettings

    var intValue: Int = 0
    var decimalValue: Int = 0

    val editingValueText: String
        get() = (intValue.toDouble() + (decimalValue.toDouble() / 10)).formatString()

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

        intValue = getCurrentValue().toInt()
        decimalValue = ((getCurrentValue() - getCurrentValue().toInt()) * 10).toInt()

        binding.intBar.isVisible = !readOnly
        binding.decimalBar.isVisible = !readOnly
        binding.vFatTextLabel.text = context.getString(measureValue.titleRes)
        binding.vMeasureUnit.text = when (measureValue.unitType) {
            MeasureValue.UnitType.PERCENT -> context.getString(R.string.unit_percent)
            MeasureValue.UnitType.WEIGHT -> context.getString(userSettings.measureSystem.weightResId)
            MeasureValue.UnitType.WIDTH -> context.getString(R.string.unit_mm)
        }

        if (!readOnly) {
            if (measureValue.unitType == MeasureValue.UnitType.WEIGHT) {
                binding.intBar.max =
                    userSettings.displayWeight(measureValue.maxScale.toDouble()).toInt()
            } else {
                binding.intBar.max = measureValue.maxScale
            }

            binding.intBar.progress = intValue
            binding.decimalBar.progress = decimalValue

            binding.intBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?, progress: Int, fromUser: Boolean
                ) {
                    intValue = progress
                    binding.fatPercentText.text = editingValueText
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    // Code to execute when the user starts dragging the SeekBar
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    setValue(callback)
                }
            })

            binding.decimalBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?, progress: Int, fromUser: Boolean
                ) {
                    decimalValue = progress
                    binding.fatPercentText.text = editingValueText
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    // Code to execute when the user starts dragging the SeekBar
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    setValue(callback)
                }
            })
        }

        binding.fatPercentText.text = editingValueText
    }

    private fun setValue(
        callback: (measureValue: MeasureValue, value: Number) -> Unit
    ) {
        val measureValue = measureValue ?: return
        callback(measureValue, intValue.toDouble() + decimalValue.toDouble() / 10)
    }

    private fun getCurrentValue(): Double {
        val measureValue = measureValue ?: return 0.0
        val measure = measure ?: return 0.0

        return measure.displayValue(measureValue, userSettings)
    }
}
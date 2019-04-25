package com.nmarsollier.fitfat.components

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.nmarsollier.fitfat.R
import com.nmarsollier.fitfat.model.Measure
import com.nmarsollier.fitfat.model.MeasureType
import com.nmarsollier.fitfat.model.MeasureValue
import com.nmarsollier.fitfat.model.UserSettings
import com.nmarsollier.fitfat.utils.formatString
import com.nmarsollier.fitfat.utils.toPounds
import kotlinx.android.synthetic.main.new_measure_fat_holder.view.*
import kotlinx.android.synthetic.main.new_measure_holder.view.*


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
        return if (viewType == 2) {
            newMeasureFatHolder(parent, context)
        } else {
            newMeasureValueHolder(parent, context)
        }
    }

    override fun onBindViewHolder(holder: MeasureHolder, position: Int) {
        holder.bind(measures[position], measure, userSettings, callback, readOnly)
    }

    override fun getItemCount(): Int {
        return measures.size
    }

    override fun getItemViewType(position: Int): Int {
        return measures[position].getHolderType()
    }

    private fun newMeasureValueHolder(parent: ViewGroup, context: Context): MeasureHolder {
        return MeasureValueHolder(
            LayoutInflater.from(context).inflate(
                R.layout.new_measure_holder,
                parent,
                false
            )
        )
    }

    private fun newMeasureFatHolder(parent: ViewGroup, context: Context): MeasureHolder {
        return MeasureFatHolder(
            LayoutInflater.from(context).inflate(
                R.layout.new_measure_fat_holder,
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
        userSettings: UserSettings?,
        callback: () -> Unit,
        readOnly: Boolean
    )
}

class MeasureValueHolder constructor(itemView: View) : MeasureHolder(itemView) {
    private var measureValue: MeasureValue? = null
    private var measure: Measure? = null

    override fun bind(
        measureValue: MeasureValue,
        measure: Measure,
        userSettings: UserSettings?,
        callback: () -> Unit,
        readOnly: Boolean
    ) {
        this.measureValue = measureValue
        this.measure = measure

        if (measureValue == MeasureValue.BODY_WEIGHT) {
            if (userSettings?.measureSystem == MeasureType.IMPERIAL) {
                itemView.vValueUnit.setText(R.string.unit_lb)
                itemView.vValueBar.max = (200.0).toPounds().toInt()
            } else {
                itemView.vValueUnit.setText(R.string.unit_kg)
                itemView.vValueBar.max = 200
            }
        } else {
            itemView.vValueBar.max = 60
            itemView.vValueUnit.setText(R.string.unit_mm)
        }

        val value = getValue()
        itemView.vValueText.text = value.formatString()

        itemView.vValueBar.isVisible = !readOnly
        itemView.vHelpIcon.isVisible = !readOnly
        itemView.vTitleLabel.text = itemView.context.getString(measureValue.titleRes)

        if (!readOnly) {
            itemView.vValueBar.progress = value

            itemView.vValueBar.setOnSeekBarChangeListener(object : SeekBarChange() {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    setValue(progress, measureValue)
                    itemView.vValueText.text = getValue().formatString()
                    callback.invoke()
                }
            })

            measureValue.helpRes?.let { helpRes ->
                itemView.vHelpIcon.isVisible = true
                itemView.vHelpIcon.setOnClickListener {
                    showHelpDialog(itemView.context, helpRes)
                }
            } ?: run {
                itemView.vHelpIcon.isVisible = false
            }
        }
    }

    fun setValue(value: Int, fromMeasureValue: MeasureValue) {
        if (fromMeasureValue != measureValue) {
            return
        }

        when (measureValue) {
            MeasureValue.CHEST -> measure?.chest = value
            MeasureValue.ABDOMINAL -> measure?.abdominal = value
            MeasureValue.THIGH -> measure?.thigh = value
            MeasureValue.TRICEP -> measure?.tricep = value
            MeasureValue.SUBSCAPULAR -> measure?.subscapular = value
            MeasureValue.SUPRAILIAC -> measure?.suprailiac = value
            MeasureValue.MIDAXILARITY -> measure?.midaxillary = value
            MeasureValue.BICEP -> measure?.bicep = value
            MeasureValue.LOWER_BACK -> measure?.lowerBack = value
            MeasureValue.CALF -> measure?.calf = value
            MeasureValue.BODY_WEIGHT -> measure?.bodyWeight = value.toDouble()
            else -> {
            }
        }
    }

    fun getValue(): Int {
        return when (measureValue) {
            MeasureValue.CHEST -> measure?.chest
            MeasureValue.ABDOMINAL -> measure?.abdominal
            MeasureValue.THIGH -> measure?.thigh
            MeasureValue.TRICEP -> measure?.tricep
            MeasureValue.SUBSCAPULAR -> measure?.subscapular
            MeasureValue.SUPRAILIAC -> measure?.suprailiac
            MeasureValue.MIDAXILARITY -> measure?.midaxillary
            MeasureValue.BICEP -> measure?.bicep
            MeasureValue.LOWER_BACK -> measure?.lowerBack
            MeasureValue.CALF -> measure?.calf
            MeasureValue.BODY_WEIGHT -> measure?.bodyWeight?.toInt() ?: 0
            else -> 0
        } ?: 0
    }
}

class MeasureFatHolder constructor(itemView: View) : MeasureHolder(itemView) {
    private var measureValue: MeasureValue? = null
    private var measure: Measure? = null

    override fun bind(
        measureValue: MeasureValue,
        measure: Measure,
        userSettings: UserSettings?,
        callback: () -> Unit,
        readOnly: Boolean
    ) {
        this.measureValue = measureValue
        this.measure = measure

        itemView.vFatValueBar.isVisible = !readOnly
        itemView.vFatValueDecimalBar.isVisible = !readOnly

        if (!readOnly) {
            itemView.vFatValueBar.progress = measure.fatPercent.toInt()
            itemView.vFatValueDecimalBar.progress = ((measure.fatPercent - measure.fatPercent.toInt()) * 100).toInt()

            itemView.vFatValueBar.setOnSeekBarChangeListener(object : SeekBarChange() {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    setFatValue(progress, measureValue, callback)
                }
            })

            itemView.vFatValueDecimalBar.setOnSeekBarChangeListener(object : SeekBarChange() {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    setFatDecimalValue(progress, measureValue, callback)
                }
            })
        }

        updateUnits(callback)
    }

    private fun setFatValue(
        progress: Int,
        fromMeasureValue: MeasureValue,
        callback: () -> Unit
    ) {
        if (fromMeasureValue != measureValue) {
            return
        }

        val measure = measure ?: return

        val decimal = measure.fatPercent - measure.fatPercent.toInt()

        measure.fatPercent = progress.toDouble() + decimal
        updateUnits(callback)
    }

    private fun setFatDecimalValue(
        progress: Int,
        fromMeasureValue: MeasureValue,
        callback: () -> Unit
    ) {
        if (fromMeasureValue != measureValue) {
            return
        }

        val measure = measure ?: return

        measure.fatPercent = measure.fatPercent.toInt().toDouble() + (progress.toDouble() / 100)
        updateUnits(callback)
    }


    fun updateUnits(callback: () -> Unit) {
        itemView.vFatValueText.text = measure?.fatPercent?.formatString() ?: ""
        callback.invoke()
    }
}
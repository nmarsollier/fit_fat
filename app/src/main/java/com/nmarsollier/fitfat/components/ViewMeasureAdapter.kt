package com.nmarsollier.fitfat.components

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.nmarsollier.fitfat.R
import com.nmarsollier.fitfat.model.Measure
import com.nmarsollier.fitfat.model.MeasureType
import com.nmarsollier.fitfat.model.MeasureValue
import com.nmarsollier.fitfat.model.UserSettings
import com.nmarsollier.fitfat.utils.formatString
import com.nmarsollier.fitfat.utils.onProgressChangeListener
import com.nmarsollier.fitfat.utils.toPounds
import kotlinx.android.synthetic.main.help_dialog.*
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
        return if (measures[position] == MeasureValue.BODY_FAT) 2 else 1
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
                itemView.vValueBar.max = measureValue.maxScale.toDouble().toPounds().toInt()
            } else {
                itemView.vValueUnit.setText(R.string.unit_kg)
                itemView.vValueBar.max = measureValue.maxScale
            }
        } else {
            itemView.vValueBar.max = measureValue.maxScale
            itemView.vValueUnit.setText(R.string.unit_mm)
        }

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
        val measureValue = measureValue ?: return 0
        val measure = measure ?: return 0
        return measure.getValueForMethod(measureValue).toInt()
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

            itemView.vFatValueBar.setOnSeekBarChangeListener(
                onProgressChangeListener { _, progress, _ ->
                    setFatValue(progress, measureValue, callback)
                }
            )

            itemView.vFatValueDecimalBar.setOnSeekBarChangeListener(
                onProgressChangeListener { _, progress, _ ->
                    setFatDecimalValue(progress, measureValue, callback)
                }
            )
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


    private fun updateUnits(callback: () -> Unit) {
        itemView.vFatValueText.text = measure?.fatPercent?.formatString() ?: ""
        callback.invoke()
    }
}
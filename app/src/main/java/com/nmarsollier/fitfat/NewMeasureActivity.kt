package com.nmarsollier.fitfat

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.text.format.DateFormat
import android.view.*
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.nmarsollier.fitfat.components.SeekBarChange
import com.nmarsollier.fitfat.model.*
import com.nmarsollier.fitfat.utils.*
import kotlinx.android.synthetic.main.new_measure_activity.*
import kotlinx.android.synthetic.main.new_measure_fat_holder.view.*
import kotlinx.android.synthetic.main.new_measure_holder.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.util.*

class NewMeasureActivity : AppCompatActivity() {
    private var userSettings: UserSettings? = null
    private var measure = Measure(UUID.randomUUID().toString(), 0.0, 0, SexType.MALE)
    private lateinit var adapter: MeasuresAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.new_measure_activity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        title = getString(R.string.new_measure_title)

        vMeasureMethod.setOnClickListener { showMeasureTypeSelectionDialog() }

        vMeasureDate.setOnClickListener { changeDateTime() }

        adapter = MeasuresAdapter(baseContext, measure, userSettings) { resId ->
            if (resId != null) {
                showHelp(resId)
            } else {
                updateFatPercent()
            }
        }

        vRecyclerView.adapter = adapter

        reloadSettings()
        loadLastMeasure()
    }

    private fun showHelp(resId: Int) {
        vHelpView.isVisible = false
        vHelpView.setOnClickListener {
            vHelpView.isVisible = false
        }

        vHelpView.setImageResource(resId)
        vHelpView.isVisible = true
    }

    private fun changeDateTime() {
        val measureDate = Calendar.getInstance()
        measureDate.time = measure.date

        DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                measureDate.set(year, monthOfYear, dayOfMonth, 0, 0, 0)

                TimePickerDialog(
                    this,
                    TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                        measureDate.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        measureDate.set(Calendar.MINUTE, minute)

                        measure.date = measureDate.time
                        refreshUI()
                    },
                    measureDate.get(Calendar.HOUR_OF_DAY),
                    measureDate.get(Calendar.MINUTE),
                    DateFormat.is24HourFormat(this)
                ).show()
            },
            measureDate.get(Calendar.YEAR),
            measureDate.get(Calendar.MONTH),
            measureDate.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun reloadSettings() {
        val context = baseContext ?: return
        GlobalScope.launch {
            userSettings = getRoomDatabase(context).userDao().getUserSettings().also {
                measure = Measure(UUID.randomUUID().toString(), it.weight, it.birthDate.getAge(), it.sex)
                adapter.userSettings = it
            }
            MainScope().launch {
                loadLastMeasure()
                refreshUI()
            }
        }
    }

    private fun loadLastMeasure() {
        val context = baseContext ?: return
        GlobalScope.launch {
            getRoomDatabase(context).measureDao().getLastMeasure()?.let { last ->
                measure.measureMethod = last.measureMethod
            }
            MainScope().launch {
                refreshUI()
            }
        }
    }

    private fun refreshUI() {
        vMeasureMethod.setText(measure.measureMethod.labelRes)
        adapter.setData(measure)
        vMeasureDate.setText(measure.date.formatDateTime())
        updateFatPercent()
    }

    private fun updateFatPercent() {
        measure.calculateFatPercent()
        vFat.text = measure.fatPercent.formatString()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_save, menu)
        updateMenuItemColor(menu, resources)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                checkEmptyAndExit()
                true
            }
            R.id.menu_save -> {
                saveAndExit()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun checkEmptyAndExit() {
        if (measure.isEmpty()) {
            finish()
        } else {
            AlertDialog.Builder(this)
                .setTitle(getString(R.string.save_dialog_title))
                .setMessage(getString(R.string.save_dialog_message))
                .setPositiveButton(
                    android.R.string.yes
                ) { _, _ ->
                    saveAndExit()
                }
                .setNegativeButton(android.R.string.no)
                { _, _ ->
                    finish()
                }.show()
        }
    }

    private fun saveAndExit() {
        val context = applicationContext ?: return
        if (measure.isValid()) {
            GlobalScope.launch {
                getRoomDatabase(context).measureDao().insert(measure)
                getRoomDatabase(context).userDao().getUserSettings().let { settings ->
                    settings.weight = measure.bodyWeight
                    getRoomDatabase(context).userDao().update(settings)
                }

                MainScope().launch {
                    finish()
                }
            }
        } else {
            Toast.makeText(context, R.string.new_measure_error, Toast.LENGTH_LONG).show()
        }
    }

    private fun showMeasureTypeSelectionDialog() {
        val measureMethods = MeasureMethod.values().map { getString(it.labelRes) }.toTypedArray()
        val selected = measure.measureMethod.ordinal

        var dialog: AlertDialog? = null
        dialog = AlertDialog.Builder(this)
            .setTitle(R.string.new_measure_method_title)
            .setSingleChoiceItems(measureMethods, selected) { _, which ->
                measure.measureMethod = MeasureMethod.values()[which]
                refreshUI()
                dialog?.dismiss()
            }
            .setNegativeButton(android.R.string.cancel, null)
            .create()
        dialog.show()
    }

    class MeasuresAdapter internal constructor(
        private val context: Context,
        private var measure: Measure,
        var userSettings: UserSettings?,
        private var callback: (helpId: Int?) -> Unit
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
            holder.bind(measures[position], measure, userSettings, callback)
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
            callback: (helpId: Int?) -> Unit
        )
    }

    class MeasureValueHolder constructor(itemView: View) : MeasureHolder(itemView) {
        private var measureValue: MeasureValue? = null
        private var measure: Measure? = null

        override fun bind(
            measureValue: MeasureValue,
            measure: Measure,
            userSettings: UserSettings?,
            callback: (helpId: Int?) -> Unit
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
            itemView.vValueBar.progress = value
            itemView.vValueText.text = value.formatString()

            itemView.vValueBar.setOnSeekBarChangeListener(object : SeekBarChange() {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    setValue(progress, measureValue)
                    itemView.vValueText.text = getValue().formatString()
                    callback.invoke(null)
                }
            })

            itemView.vTitleLabel.text = itemView.context.getString(measureValue.titleRes)

            if (measureValue.helpRes != null) {
                itemView.vHelpIcon.isVisible = true
                itemView.vHelpIcon.setOnClickListener {
                    callback.invoke(measureValue.helpRes)
                }
            } else {
                itemView.vHelpIcon.isVisible = false
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
            callback: (helpId: Int?) -> Unit
        ) {
            this.measureValue = measureValue
            this.measure = measure

            itemView.vFatValueBar.progress = measure.fatPercent.toInt()
            itemView.vFatValueDecimalBar.progress = ((measure.fatPercent + measure.fatPercent.toInt()) * 100).toInt()
            updateUnits(callback)

            itemView.vFatValueBar.setOnSeekBarChangeListener(object : SeekBarChange() {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    val decimal = measure.fatPercent - measure.fatPercent.toInt()

                    measure.fatPercent = progress.toDouble() + decimal
                    updateUnits(callback)
                }
            })

            itemView.vFatValueDecimalBar.setOnSeekBarChangeListener(object : SeekBarChange() {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    measure.fatPercent = measure.fatPercent.toInt().toDouble() + (progress.toDouble() / 100)
                    updateUnits(callback)
                }
            })
        }

        fun updateUnits(callback: (helpId: Int?) -> Unit) {
            itemView.vFatValueText.text = measure?.fatPercent?.formatString() ?: ""
            callback.invoke(null)
        }
    }
}

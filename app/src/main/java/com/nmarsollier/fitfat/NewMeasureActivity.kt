package com.nmarsollier.fitfat

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.nmarsollier.fitfat.components.SeekBarChange
import com.nmarsollier.fitfat.model.*
import com.nmarsollier.fitfat.utils.formatString
import com.nmarsollier.fitfat.utils.getAge
import com.nmarsollier.fitfat.utils.updateMenuItemColor
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
    private var adapter = MeasuresAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.new_measure_activity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        title = getString(R.string.new_measure_title)

        vMeasureMethod.adapter =
            ArrayAdapter<MeasureMethod>(this, android.R.layout.simple_spinner_item, MeasureMethod.values())

        vMeasureMethod.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                measure.measureMethod = MeasureMethod.values()[position]
                refreshUI()
            }
        }

        vRecyclerView.adapter = adapter

        reloadSettings()
        loadLastMeasure()
    }

    private fun reloadSettings() {
        val context = baseContext ?: return
        GlobalScope.launch {
            userSettings = getRoomDatabase(context).userDao().getUserSettings().also {
                measure = Measure(UUID.randomUUID().toString(), it.weight, it.birthDate.getAge(), it.sex)
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
        vMeasureMethod.setSelection(measure.measureMethod.ordinal)
        adapter.notifyDataSetChanged()
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

                MainScope().launch {
                    finish()
                }
            }
        } else {
            Toast.makeText(context, R.string.new_measure_error, Toast.LENGTH_LONG).show()
        }
    }

    enum class MeasureValue {
        CHEST,
        ABDOMINAL,
        THIGH,
        TRICEP,
        SUBSCAPULAR,
        SUPRAILIAC,
        MIDAXILARITY,
        BICEP,
        LOWER_BACK,
        CALF,
        BODY_FAT;

        fun getHolderType(): Int {
            return if (this == BODY_FAT) 2 else 1
        }

        fun isRequired(method: MeasureMethod): Boolean {
            return when (this) {
                CHEST -> method.chestRequired()
                ABDOMINAL -> method.abdominalRequired()
                THIGH -> method.thighRequired()
                TRICEP -> method.tricepRequired()
                SUBSCAPULAR -> method.subscapularRequired()
                SUPRAILIAC -> method.suprailiacRequired()
                MIDAXILARITY -> method.midaxillaryRequired()
                BICEP -> method.bicepRequired()
                LOWER_BACK -> method.lowerBackRequired()
                CALF -> method.calfRequired()
                BODY_FAT -> method.fatPercentRequired()
            }
        }

        fun getTitleRes(): Int {
            return when (this) {
                CHEST -> R.string.measure_chest
                ABDOMINAL -> R.string.measure_abdominal
                THIGH -> R.string.measure_thigh
                TRICEP -> R.string.measure_tricep
                SUBSCAPULAR -> R.string.measure_subscapular
                SUPRAILIAC -> R.string.measure_suprailiac
                MIDAXILARITY -> R.string.measure_midaxillary
                BICEP -> R.string.measure_bicep
                LOWER_BACK -> R.string.measure_lower_back
                CALF -> R.string.measure_calf
                BODY_FAT -> R.string.measure_fat
            }
        }
    }

    inner class MeasuresAdapter internal constructor() : RecyclerView.Adapter<MeasureHolder>() {
        private var measures = mutableListOf<MeasureValue>()

        init {
            registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
                override fun onChanged() {
                    val method = measure.measureMethod
                    measures = MeasureValue.values().filter { it.isRequired(method) }.toMutableList()
                    super.onChanged()
                }
            })
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeasureHolder {
            return if (viewType == 2) {
                newMeasureFatHolder(parent, baseContext)
            } else {
                newMeasureValueHolder(parent, baseContext)
            }
        }

        override fun onBindViewHolder(holder: MeasureHolder, position: Int) {
            holder.bind(measures[position], measure)
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

    abstract inner class MeasureHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bind(measureValue: MeasureValue, measure: Measure)
    }

    inner class MeasureValueHolder constructor(itemView: View) : MeasureHolder(itemView) {
        var measureValue: MeasureValue? = null
        var measure: Measure? = null

        override fun bind(measureValue: MeasureValue, measure: Measure) {
            this.measureValue = measureValue
            this.measure = measure

            val value = getValue()
            itemView.vValueBar.progress = value
            itemView.vValueText.text = value.formatString()

            itemView.vValueBar.setOnSeekBarChangeListener(object : SeekBarChange() {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    setValue(progress)
                    itemView.vValueText.text = getValue().formatString()
                    updateFatPercent()
                }
            })

            itemView.vTitleLabel.text = getString(measureValue.getTitleRes())
        }

        fun setValue(value: Int) {
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
                else -> 0
            } ?: 0
        }

    }

    inner class MeasureFatHolder constructor(itemView: View) : MeasureHolder(itemView) {
        override fun bind(measureValue: MeasureValue, measure: Measure) {
            itemView.vFatValueBar.progress = measure.fatPercent.toInt()
            itemView.vFatValueDecimalBar.progress = ((measure.fatPercent + measure.fatPercent.toInt()) * 100).toInt()
            updateUnits()

            itemView.vFatValueBar.setOnSeekBarChangeListener(object : SeekBarChange() {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    val decimal = measure.fatPercent - measure.fatPercent.toInt()

                    measure.fatPercent = progress.toDouble() + decimal
                    updateUnits()
                }
            })

            itemView.vFatValueDecimalBar.setOnSeekBarChangeListener(object : SeekBarChange() {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    measure.fatPercent = measure.fatPercent.toInt().toDouble() + (progress.toDouble() / 100)
                    updateUnits()
                }
            })
        }

        fun updateUnits() {
            itemView.vFatValueText.text = measure.fatPercent.formatString()
            updateFatPercent()
        }
    }
}

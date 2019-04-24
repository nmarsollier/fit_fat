package com.nmarsollier.fitfat

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.nmarsollier.fitfat.components.SeekBarChange
import com.nmarsollier.fitfat.model.*
import com.nmarsollier.fitfat.utils.formatString
import com.nmarsollier.fitfat.utils.getAge
import com.nmarsollier.fitfat.utils.updateMenuItemColor
import kotlinx.android.synthetic.main.add_measure_activity.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.util.*

class NewMeasureActivity : AppCompatActivity() {
    private var userSettings: UserSettings? = null
    private var measure = Measure(UUID.randomUUID().toString(), 0.0, 0, SexType.MALE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_measure_activity)
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

        vChest.setOnSeekBarChangeListener(object : SeekBarChange() {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                measure.chest = progress
                updateUnits()
            }
        })

        vAbdominal.setOnSeekBarChangeListener(object : SeekBarChange() {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                measure.abdominal = progress
                updateUnits()
            }
        })

        vThigh.setOnSeekBarChangeListener(object : SeekBarChange() {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                measure.thigh = progress
                updateUnits()
            }
        })

        vTricep.setOnSeekBarChangeListener(object : SeekBarChange() {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                measure.tricep = progress
                updateUnits()
            }
        })

        vSubscapular.setOnSeekBarChangeListener(object : SeekBarChange() {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                measure.subscapular = progress
                updateUnits()
            }
        })

        vSuprailiac.setOnSeekBarChangeListener(object : SeekBarChange() {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                measure.suprailiac = progress
                updateUnits()
            }
        })

        vMidaxillary.setOnSeekBarChangeListener(object : SeekBarChange() {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                measure.midaxillary = progress
                updateUnits()
            }
        })

        vBicep.setOnSeekBarChangeListener(object : SeekBarChange() {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                measure.bicep = progress
                updateUnits()
            }
        })

        vLowerBack.setOnSeekBarChangeListener(object : SeekBarChange() {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                measure.lowerBack = progress
                updateUnits()
            }
        })

        vCalf.setOnSeekBarChangeListener(object : SeekBarChange() {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                measure.calf = progress
                updateUnits()
            }
        })

        vBodyFat.setOnSeekBarChangeListener(object : SeekBarChange() {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val decimal = measure.fatPercent - measure.fatPercent.toInt()

                measure.fatPercent = progress.toDouble() + decimal
                updateUnits()
            }
        })

        vBodyFatDecimal.setOnSeekBarChangeListener(object : SeekBarChange() {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                measure.fatPercent = measure.fatPercent.toInt().toDouble() + (progress.toDouble() / 100)
                updateUnits()
            }
        })

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

        vChestView.isVisible = measure.measureMethod.chestRequired()
        vAbdominalView.isVisible = measure.measureMethod.abdominalRequired()
        vThighView.isVisible = measure.measureMethod.thighRequired()
        vTricepView.isVisible = measure.measureMethod.tricepRequired()
        vSubscapularView.isVisible = measure.measureMethod.subscapularRequired()
        vSuprailiacView.isVisible = measure.measureMethod.suprailiacRequired()
        vMidaxillaryView.isVisible = measure.measureMethod.midaxillaryRequired()
        vBicepView.isVisible = measure.measureMethod.bicepRequired()
        vLowerBackView.isVisible = measure.measureMethod.lowerBackRequired()
        vCalfView.isVisible = measure.measureMethod.calfRequired()
        vBodyFatView.isVisible = measure.measureMethod.fatPercentRequired()

        updateUnits()
        updateBars()
    }

    private fun updateUnits() {
        measure.calculateFatPercent()

        vChestText.text = measure.chest.formatString()
        vAbdominalText.text = measure.abdominal.formatString()
        vThighText.text = measure.thigh.formatString()
        vTricepText.text = measure.tricep.formatString()
        vSubscapularText.text = measure.subscapular.formatString()
        vSuprailiacText.text = measure.suprailiac.formatString()
        vMidaxillaryText.text = measure.midaxillary.formatString()
        vBicepText.text = measure.bicep.formatString()
        vLowerBackText.text = measure.lowerBack.formatString()
        vCalfText.text = measure.calf.formatString()
        vBodyFatText.text = measure.fatPercent.formatString()

        vFat.text = measure.fatPercent.formatString()
    }

    private fun updateBars() {
        vChest.progress = measure.chest
        vAbdominal.progress = measure.abdominal
        vThigh.progress = measure.thigh
        vTricep.progress = measure.tricep
        vSubscapular.progress = measure.subscapular
        vSuprailiac.progress = measure.suprailiac
        vMidaxillary.progress = measure.midaxillary
        vBicep.progress = measure.bicep
        vLowerBack.progress = measure.lowerBack
        vCalf.progress = measure.calf
        vBodyFat.progress = measure.fatPercent.toInt()
        vBodyFatDecimal.progress = ((measure.fatPercent + measure.fatPercent.toInt()) * 100).toInt()
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
}

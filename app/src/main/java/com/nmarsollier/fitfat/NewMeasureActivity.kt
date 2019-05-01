package com.nmarsollier.fitfat

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.format.DateFormat
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.crashlytics.android.Crashlytics
import com.evernote.android.state.State
import com.nmarsollier.fitfat.components.MeasuresAdapter
import com.nmarsollier.fitfat.model.*
import com.nmarsollier.fitfat.utils.*
import io.fabric.sdk.android.Fabric
import kotlinx.android.synthetic.main.new_measure_activity.*
import java.util.*

class NewMeasureActivity : AppCompatActivity() {
    @State
    var userSettings: UserSettings? = null

    @State
    var measure = Measure(UUID.randomUUID().toString(), 0.0, 0, SexType.MALE)

    private lateinit var adapter: MeasuresAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Fabric.with(this, Crashlytics())

        setContentView(R.layout.new_measure_activity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        title = getString(R.string.new_measure_title)

        vMeasureMethod.setOnClickListener { showMeasureTypeSelectionDialog() }

        vMeasureDate.setOnClickListener { changeDateTime() }

        adapter = MeasuresAdapter(baseContext, measure, userSettings, false) { updateFatPercent() }

        vRecyclerView.adapter = adapter

        loadUserSettings()
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

    private fun loadUserSettings() {
        val context = baseContext ?: return
        if (userSettings != null) {
            refreshUI()
            return
        }

        runInBackground {
            userSettings = getRoomDatabase(context).userDao().getUserSettings().also {
                measure = Measure(UUID.randomUUID().toString(), it.weight, it.birthDate.getAge(), it.sex)
                adapter.userSettings = it
            }
            runInForeground {
                loadLastMeasure()
                refreshUI()
            }
        }
    }

    private fun loadLastMeasure() {
        val context = baseContext ?: return
        runInBackground {
            getRoomDatabase(context).measureDao().getLastMeasure()?.let { last ->
                measure.measureMethod = last.measureMethod
            }
            runInForeground {
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
        updateMenuItemColor(menu)

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
            runInBackground {
                getRoomDatabase(context).measureDao().insert(measure)
                getRoomDatabase(context).userDao().getUserSettings().let { settings ->
                    settings.weight = measure.bodyWeight
                    getRoomDatabase(context).userDao().update(settings)
                }
                FirebaseDao.uploadPendingMeasures(context)

                runInForeground {
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

    companion object {
        fun startActivity(context: Context) {
            ContextCompat.startActivity(context, Intent(context, NewMeasureActivity::class.java), null)
        }
    }
}

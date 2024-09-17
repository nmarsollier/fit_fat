package com.nmarsollier.fitfat.ui.editMeasure

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
import androidx.lifecycle.lifecycleScope
import com.nmarsollier.fitfat.R
import com.nmarsollier.fitfat.common.uiUtils.labelRes
import com.nmarsollier.fitfat.common.uiUtils.updateMenuItemColor
import com.nmarsollier.fitfat.common.utils.formatDateTime
import com.nmarsollier.fitfat.common.utils.formatString
import com.nmarsollier.fitfat.databinding.NewMeasureActivityBinding
import com.nmarsollier.fitfat.models.measures.Measure
import com.nmarsollier.fitfat.models.measures.db.MeasureMethod
import com.nmarsollier.fitfat.ui.editMeasure.ui.MeasuresAdapter
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.getViewModel
import java.util.Calendar

class NewMeasureActivity : AppCompatActivity() {
    private val binding by lazy {
        NewMeasureActivityBinding.inflate(layoutInflater)
    }

    private val viewModel by lazy {
        getViewModel<EditMeasureViewModel>()
    }

    private lateinit var adapter: MeasuresAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        title = getString(R.string.new_measure_title)

        lifecycleScope.launch {
            viewModel.event.collect { event ->
                when (event) {
                    EditMeasureEvent.Close -> finish()
                    EditMeasureEvent.Invalid -> {
                        Toast.makeText(
                            applicationContext, R.string.new_measure_error, Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
        lifecycleScope.launch {
            viewModel.state.collect { state ->
                when (state) {
                    is EditMeasureState.Ready -> {
                        adapter = MeasuresAdapter(
                            baseContext, state.measure, state.userSettings, false
                        ) { measureValue, value ->
                            viewModel.reduce(
                                EditMeasureAction.UpdateMeasureValue(
                                    measureValue, value
                                )
                            )
                        }
                        refreshUI(state.measure)

                        binding.measureMethod.setOnClickListener {
                            showMeasureTypeSelectionDialog(
                                state.measure
                            )
                        }
                        binding.measureDate.setOnClickListener { changeDateTime(state.measure) }
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun changeDateTime(measure: Measure) {
        val measureDate = Calendar.getInstance()
        measureDate.time = measure.date

        DatePickerDialog(
            this,
            { _, year, monthOfYear, dayOfMonth ->
                measureDate.set(year, monthOfYear, dayOfMonth, 0, 0, 0)

                TimePickerDialog(
                    this,
                    { _, hourOfDay, minute ->
                        measureDate.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        measureDate.set(Calendar.MINUTE, minute)

                        viewModel.reduce(EditMeasureAction.UpdateDate(measureDate.time))
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

    private fun refreshUI(measure: Measure) {
        binding.recyclerView.adapter = adapter

        binding.measureMethod.setText(measure.measureMethod.labelRes)
        adapter.setData(measure)
        binding.measureDate.setText(measure.date.formatDateTime)
        binding.fat.text = measure.fatPercent.formatString()
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
                viewModel.reduce(EditMeasureAction.SaveMeasure)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun checkEmptyAndExit() {
        AlertDialog.Builder(this).setTitle(getString(R.string.save_dialog_title))
            .setMessage(getString(R.string.save_dialog_message)).setPositiveButton(
                android.R.string.yes
            ) { _, _ ->
                viewModel.reduce(EditMeasureAction.SaveMeasure)
            }.setNegativeButton(android.R.string.no) { _, _ ->
                finish()
            }.show()
    }

    private fun showMeasureTypeSelectionDialog(measure: Measure) {
        val measureMethods = MeasureMethod.entries.map { getString(it.labelRes) }.toTypedArray()
        val selected = measure.measureMethod.ordinal

        AlertDialog.Builder(this).setTitle(R.string.new_measure_method_title)
            .setSingleChoiceItems(measureMethods, selected) { dialog, which ->
                viewModel.reduce(EditMeasureAction.UpdateMeasureMethod(MeasureMethod.entries[which]))
                dialog.dismiss()
            }.setNegativeButton(android.R.string.cancel, null).create().also {
                it.show()
            }
    }

    companion object {
        fun startActivity(context: Context) {
            ContextCompat.startActivity(
                context, Intent(context, NewMeasureActivity::class.java), null
            )
        }
    }
}

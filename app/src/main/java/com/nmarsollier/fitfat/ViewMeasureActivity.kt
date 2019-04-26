package com.nmarsollier.fitfat

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.evernote.android.state.State
import com.nmarsollier.fitfat.components.MeasuresAdapter
import com.nmarsollier.fitfat.model.Measure
import com.nmarsollier.fitfat.model.UserSettings
import com.nmarsollier.fitfat.model.getRoomDatabase
import com.nmarsollier.fitfat.utils.formatDateTime
import com.nmarsollier.fitfat.utils.formatString
import com.nmarsollier.fitfat.utils.runInBackground
import com.nmarsollier.fitfat.utils.runInForeground
import kotlinx.android.synthetic.main.new_measure_activity.*


class ViewMeasureActivity : AppCompatActivity() {
    private var userSettings: UserSettings? = null

    @State
    lateinit var measure: Measure

    private lateinit var adapter: MeasuresAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        measure = intent.getParcelableExtra("measure")

        setContentView(R.layout.new_measure_activity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        title = getString(R.string.new_measure_title)

        adapter = MeasuresAdapter(baseContext, measure, userSettings, true) { updateFatPercent() }

        vRecyclerView.adapter = adapter

        reloadSettings()
    }

    private fun reloadSettings() {
        val context = baseContext ?: return
        runInBackground {
            userSettings = getRoomDatabase(context).userDao().getUserSettings().also {
                adapter.userSettings = it
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        fun startActivity(context: Context, measure: Measure) {
            val intent = Intent(context, ViewMeasureActivity::class.java)
            intent.putExtra("measure", measure)
            ContextCompat.startActivity(context, intent, null)
        }
    }
}

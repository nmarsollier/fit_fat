package com.nmarsollier.fitfat.ui.measures

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.nmarsollier.fitfat.R
import com.nmarsollier.fitfat.databinding.NewMeasureActivityBinding
import com.nmarsollier.fitfat.model.measures.Measure
import com.nmarsollier.fitfat.utils.formatDateTime
import com.nmarsollier.fitfat.utils.formatString
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ViewMeasureActivity : AppCompatActivity() {
    private val binding by lazy {
        NewMeasureActivityBinding.inflate(layoutInflater)
    }

    private val viewModel by viewModels<ViewMeasureViewModel>()

    private lateinit var adapter: MeasuresAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            viewModel.state.collect {
                when (it) {
                    is ViewMeasureState.Loading -> Unit
                    is ViewMeasureState.Ready -> {
                        adapter = MeasuresAdapter(
                            baseContext,
                            it.measure,
                            it.userSettings,
                            true
                        ) { _, _ ->
                            updateFatPercent(it)
                        }

                        binding.recyclerView.adapter = adapter

                        refreshUI(it)
                    }
                }
            }
        }

        viewModel.initialize(intent.getParcelableExtra<Measure>("measure")!!)

        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        title = getString(R.string.new_measure_title)
    }


    private fun refreshUI(state: ViewMeasureState.Ready) = MainScope().launch {
        binding.measureMethod.setText(state.measure.measureMethod.labelRes)
        adapter.setData(state.measure)
        binding.measureDate.setText(state.measure.date.formatDateTime())
        updateFatPercent(state)
    }

    private fun updateFatPercent(state: ViewMeasureState.Ready) {
        state.measure.calculateFatPercent()
        binding.fat.text = state.measure.fatPercent.formatString()
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

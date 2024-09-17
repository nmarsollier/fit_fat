package com.nmarsollier.fitfat.ui.editMeasure

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.nmarsollier.fitfat.R
import com.nmarsollier.fitfat.common.uiUtils.labelRes
import com.nmarsollier.fitfat.common.utils.formatDateTime
import com.nmarsollier.fitfat.common.utils.formatString
import com.nmarsollier.fitfat.databinding.NewMeasureActivityBinding
import com.nmarsollier.fitfat.models.measures.Measure
import com.nmarsollier.fitfat.models.measures.recalculateFatPercent
import com.nmarsollier.fitfat.ui.editMeasure.ui.MeasuresAdapter
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.getViewModel

class ViewMeasureActivity : AppCompatActivity() {
    private val binding by lazy {
        NewMeasureActivityBinding.inflate(layoutInflater)
    }

    private val viewModel by lazy {
        getViewModel<EditMeasureViewModel>()
    }

    private lateinit var adapter: MeasuresAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            viewModel.state.collect {
                when (it) {
                    is EditMeasureState.Loading -> Unit
                    is EditMeasureState.Ready -> {
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

        viewModel.reduce(EditMeasureAction.Initialize(intent.getParcelableExtra<Measure>("measure")!!))

        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        title = getString(R.string.new_measure_title)
    }


    private fun refreshUI(state: EditMeasureState.Ready) = MainScope().launch {
        binding.measureMethod.setText(state.measure.measureMethod.labelRes)
        adapter.setData(state.measure)
        binding.measureDate.setText(state.measure.date.formatDateTime)
        updateFatPercent(state)
    }

    private fun updateFatPercent(state: EditMeasureState.Ready) {
        state.measure.recalculateFatPercent()
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

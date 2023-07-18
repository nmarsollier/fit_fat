package com.nmarsollier.fitfat.ui.stats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.nmarsollier.fitfat.R
import com.nmarsollier.fitfat.databinding.MainStatsFragmentBinding
import com.nmarsollier.fitfat.model.measures.MeasureMethod
import com.nmarsollier.fitfat.model.measures.MeasureValue
import com.nmarsollier.fitfat.ui.utils.observe

class StatsFragment : Fragment() {
    private val binding: MainStatsFragmentBinding by lazy {
        MainStatsFragmentBinding.inflate(layoutInflater)
    }

    private val viewModel by viewModels<StatsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.measureMethod.setOnClickListener { showMeasureTypeSelectionDialog() }

        viewModel.state.observe(lifecycleScope) {
            when (it) {
                is StatsState.Ready -> {
                    initAdapter(it)
                    refreshUI(it)
                }
                else -> Unit
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.load(requireContext())
    }

    private fun initAdapter(state: StatsState.Ready) {
        val context = context ?: return

        val values = MeasureValue.values()
            .filter {
                if (state.selectedMethod == MeasureMethod.WEIGHT_ONLY) {
                    it.isRequired(state.selectedMethod)
                } else {
                    it.isRequired(state.selectedMethod) || it == MeasureValue.BODY_FAT
                }
            }

        binding.recyclerView.adapter = MeasureAdapter(
            context,
            state.userSettings,
            values,
            state.measures
        )
    }

    private fun showMeasureTypeSelectionDialog() {
        val context = context ?: return

        val state = (viewModel.state.value as? StatsState.Ready) ?: return

        val measureMethods = MeasureMethod.values().map { getString(it.labelRes) }.toTypedArray()

        AlertDialog.Builder(context)
            .setTitle(R.string.new_measure_method_title)
            .setSingleChoiceItems(measureMethods, state.selectedMethod.ordinal) { dialog, which ->
                viewModel.updateMethod(MeasureMethod.values()[which])
                dialog.dismiss()
            }
            .setNegativeButton(android.R.string.cancel, null)
            .create().also {
                it.show()
            }
    }

    private fun refreshUI(state: StatsState.Ready) {
        val selectedMethod = state.selectedMethod
        binding.measureMethod.setText(selectedMethod.labelRes)
    }

    companion object {
        fun newInstance() = StatsFragment()
    }
}

package com.nmarsollier.fitfat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.nmarsollier.fitfat.model.MeasureMethod
import kotlinx.android.synthetic.main.main_stats_fragment.*


class MainStats : Fragment() {
    var selectedMethod = MeasureMethod.WEIGHT_ONLY

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.main_stats_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vMeasureMethod.setOnClickListener { showMeasureTypeSelectionDialog() }

        refreshUI()
    }

    private fun showMeasureTypeSelectionDialog() {
        val context = context ?: return

        val measureMethods = MeasureMethod.values().map { getString(it.labelRes) }.toTypedArray()

        var dialog: AlertDialog? = null
        dialog = AlertDialog.Builder(context)
            .setTitle(R.string.new_measure_method_title)
            .setSingleChoiceItems(measureMethods, selectedMethod.ordinal) { _, which ->
                selectedMethod = MeasureMethod.values()[which]
                refreshUI()
                dialog?.dismiss()
            }
            .setNegativeButton(android.R.string.cancel, null)
            .create()
        dialog.show()
    }

    private fun refreshUI() {
        vMeasureMethod.setText(selectedMethod.labelRes)

    }

    companion object {
        fun newInstance() = MainStats()
    }
}

package com.nmarsollier.fitfat.ui.measuresList

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.nmarsollier.fitfat.R
import com.nmarsollier.fitfat.common.uiUtils.displayWeight
import com.nmarsollier.fitfat.common.uiUtils.labelRes
import com.nmarsollier.fitfat.common.uiUtils.weightResId
import com.nmarsollier.fitfat.common.utils.formatDateTime
import com.nmarsollier.fitfat.common.utils.formatString
import com.nmarsollier.fitfat.databinding.MainHomeMeasureHolderBinding
import com.nmarsollier.fitfat.models.measures.Measure
import com.nmarsollier.fitfat.models.userSettings.UserSettings
import com.nmarsollier.fitfat.ui.editMeasure.ViewMeasureActivity
import kotlinx.android.extensions.LayoutContainer
import kotlinx.coroutines.launch

@SuppressLint("NotifyDataSetChanged")
class MeasureAdapter internal constructor(
    private val fragment: Fragment,
    private val viewModel: MeasuresListViewModel
) : RecyclerView.Adapter<MeasureHolder>() {
    private var measures: List<Measure> = emptyList()
    private var userSettings: UserSettings? = null

    init {
        fragment.lifecycleScope.launch {
            viewModel.state.collect {
                when (it) {
                    MeasuresListState.Loading -> Unit
                    is MeasuresListState.Ready -> {
                        userSettings = it.userSettings
                        measures = it.measures
                        notifyDataSetChanged()
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeasureHolder {
        return MeasureHolder(fragment, viewModel)
    }

    override fun onBindViewHolder(holder: MeasureHolder, position: Int) {
        val userSettings = userSettings ?: return
        holder.bind(userSettings, measures[position])
    }

    override fun getItemCount() = measures.size
}

class MeasureHolder(
    private val fragment: Fragment,
    private val viewModel: MeasuresListViewModel,
    private val binding: MainHomeMeasureHolderBinding =
        MainHomeMeasureHolderBinding.inflate(
            LayoutInflater.from(fragment.requireContext()), null, false
        )
) : RecyclerView.ViewHolder(binding.root), LayoutContainer {
    private lateinit var userSettings: UserSettings

    override val containerView: View
        get() = binding.root

    fun bind(userSettings: UserSettings, measure: Measure) {
        val context = binding.root.context

        this.userSettings = userSettings

        binding.date.text = measure.date.formatDateTime
        binding.method.setText(measure.measureMethod.labelRes)

        // Fat Percent
        val fat = measure.fatPercent
        binding.fatLabel.isVisible = fat > 0
        binding.fat.isVisible = fat > 0
        binding.fatSymbol.isVisible = fat > 0
        binding.bodyFatSepatator.isVisible = fat > 0
        binding.bodyFat.isVisible = fat > 0
        binding.bodyFatUnit.isVisible = fat > 0

        binding.fat.text = fat.formatString()
        binding.userWeight.text =
            userSettings.displayWeight(measure.bodyWeight).formatString()
        binding.unit.text = context.getString(userSettings.measureSystem.weightResId)
        binding.bodyFat.text =
            userSettings.displayWeight(measure.bodyFatMass).formatString()
        binding.bodyFatUnit.text = context.getString(userSettings.measureSystem.weightResId)

        // Free Fat Mass
        val freeFatMass = measure.leanWeight
        binding.freeFatMassLabel.isVisible = freeFatMass > 0
        binding.freeFatMass.isVisible = freeFatMass > 0
        binding.freeFatMassUnit.isVisible = freeFatMass > 0
        binding.freeFatMass.text =
            userSettings.displayWeight(freeFatMass).formatString()
        binding.freeFatMassUnit.text = context.getString(userSettings.measureSystem.weightResId)

        // FFMI
        val freeFatMassIndex = measure.freeFatMassIndex
        binding.ffmiLabel.isVisible = freeFatMassIndex > 0
        binding.ffmi.isVisible = freeFatMassIndex > 0
        binding.ffmi.text = freeFatMassIndex.formatString()

        itemView.setOnLongClickListener {
            AlertDialog.Builder(context).setTitle(context.getString(R.string.measure_delete_title))
                .setMessage(context.getString(R.string.measure_delete_message)).setPositiveButton(
                    android.R.string.yes
                ) { _, _ ->
                    viewModel.reduce(MeasuresListAction.DeleteMeasure(measure))
                }.setNegativeButton(
                    android.R.string.no
                ) { _, _ ->
                }.show()
            true
        }

        itemView.setOnClickListener {
            ViewMeasureActivity.startActivity(context, measure)
        }
    }
}
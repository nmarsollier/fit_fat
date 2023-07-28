package com.nmarsollier.fitfat.ui.home

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.nmarsollier.fitfat.R
import com.nmarsollier.fitfat.databinding.MainHomeMeasureHolderBinding
import com.nmarsollier.fitfat.model.measures.Measure
import com.nmarsollier.fitfat.model.userSettings.UserSettings
import com.nmarsollier.fitfat.ui.measures.ViewMeasureActivity
import com.nmarsollier.fitfat.utils.formatDateTime
import com.nmarsollier.fitfat.utils.formatString
import com.nmarsollier.fitfat.utils.ifNotNull
import kotlinx.android.extensions.LayoutContainer
import kotlinx.coroutines.launch

@SuppressLint("NotifyDataSetChanged")
class MeasureAdapter internal constructor(
    private val fragment: HomeFragment
) : RecyclerView.Adapter<MeasureHolder>() {
    private var measures: List<Measure> = emptyList()
    private var userSettings: UserSettings? = null

    init {
        fragment.lifecycleScope.launch {
            fragment.viewModel.state.collect {
                (it as? HomeState.Ready)?.let { state ->
                    userSettings = state.userSettings
                    measures = state.measures
                    notifyDataSetChanged()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeasureHolder {
        return MeasureHolder.newInstance(fragment)
    }

    override fun onBindViewHolder(holder: MeasureHolder, position: Int) {
        ifNotNull(userSettings, measures[position]) { userSettings, measure ->
            holder.bind(userSettings, measure)
        }
    }

    override fun getItemCount() = measures.size
}

class MeasureHolder(
    private val viewModel: HomeViewModel, private val binding: MainHomeMeasureHolderBinding
) : RecyclerView.ViewHolder(binding.root), LayoutContainer {
    private lateinit var userSettings: UserSettings

    override val containerView: View
        get() = binding.root

    fun bind(userSettings: UserSettings, measure: Measure) {
        val context = binding.root.context

        this.userSettings = userSettings

        binding.date.text = measure.date.formatDateTime()
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
            userSettings.measureSystem.displayWeight(measure.bodyWeight).formatString()
        binding.unit.text = context.getString(userSettings.measureSystem.weightResId)
        binding.bodyFat.text =
            userSettings.measureSystem.displayWeight(measure.bodyFatMass).formatString()
        binding.bodyFatUnit.text = context.getString(userSettings.measureSystem.weightResId)

        // Free Fat Mass
        val freeFatMass = measure.leanWeight
        binding.freeFatMassLabel.isVisible = freeFatMass > 0
        binding.freeFatMass.isVisible = freeFatMass > 0
        binding.freeFatMassUnit.isVisible = freeFatMass > 0
        binding.freeFatMass.text =
            userSettings.measureSystem.displayWeight(freeFatMass).formatString()
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
                    viewModel.deleteMeasure(measure)
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

    companion object {
        fun newInstance(fragment: HomeFragment): MeasureHolder {
            return MeasureHolder(
                fragment.viewModel, MainHomeMeasureHolderBinding.inflate(
                    LayoutInflater.from(fragment.requireContext()), fragment.binding.root, false
                )
            )
        }
    }
}
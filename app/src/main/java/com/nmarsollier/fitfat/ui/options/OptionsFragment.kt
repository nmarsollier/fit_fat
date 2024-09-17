package com.nmarsollier.fitfat.ui.options

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.nmarsollier.fitfat.R
import com.nmarsollier.fitfat.common.uiUtils.closeProgressDialog
import com.nmarsollier.fitfat.common.uiUtils.displayHeight
import com.nmarsollier.fitfat.common.uiUtils.displayWeight
import com.nmarsollier.fitfat.common.uiUtils.heightResId
import com.nmarsollier.fitfat.common.uiUtils.showDatePicker
import com.nmarsollier.fitfat.common.uiUtils.showProgressDialog
import com.nmarsollier.fitfat.common.uiUtils.updateMenuItemColor
import com.nmarsollier.fitfat.common.uiUtils.weightResId
import com.nmarsollier.fitfat.common.utils.formatDate
import com.nmarsollier.fitfat.common.utils.formatString
import com.nmarsollier.fitfat.databinding.MainOptionsFragmentBinding
import com.nmarsollier.fitfat.models.userSettings.db.MeasureType
import com.nmarsollier.fitfat.models.userSettings.db.SexType
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.getViewModel

class OptionsFragment : Fragment() {
    private val binding by lazy {
        MainOptionsFragmentBinding.inflate(layoutInflater)
    }

    private val viewModel by lazy {
        getViewModel<OptionsViewModel>()
    }

    var menuEnabled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ) = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_save, menu)
                menu.findItem(R.id.menu_save).isEnabled = menuEnabled
                context?.updateMenuItemColor(menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.menu_save -> {
                        viewModel.reduce(OptionsAction.SaveSettings)
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        lifecycleScope.launch {
            viewModel.state.collect { state ->
                when (state) {
                    is OptionsState.Ready -> {
                        refreshUI(state)
                        closeProgressDialog()
                    }

                    OptionsState.Loading -> {
                        showProgressDialog()
                    }
                }
            }
        }

        binding.displayName.doOnTextChanged { text, _, _, _ ->
            viewModel.reduce(OptionsAction.UpdateDisplayName(text.toString()))
        }

        binding.userHeight.doOnTextChanged { text, _, _, _ ->
            viewModel.reduce(OptionsAction.UpdateHeight(text.toString().toDoubleOrNull() ?: 0.0))
        }

        binding.userWeight.doOnTextChanged { text, _, _, _ ->
            viewModel.reduce(OptionsAction.UpdateWeight(text.toString().toDoubleOrNull() ?: 0.0))
        }

        binding.measureImperial.setOnClickListener {
            viewModel.reduce(OptionsAction.UpdateMeasureSystem(MeasureType.IMPERIAL))
        }

        binding.measureMetric.setOnClickListener {
            viewModel.reduce(OptionsAction.UpdateMeasureSystem(MeasureType.METRIC))
        }

        binding.sexFemale.setOnClickListener {
            viewModel.reduce(OptionsAction.UpdateSex(SexType.FEMALE))
        }

        binding.vSexMale.setOnClickListener {
            viewModel.reduce(OptionsAction.UpdateSex(SexType.MALE))
        }
    }

    private fun refreshUI(state: OptionsState.Ready) {
        activity?.invalidateOptionsMenu()

        binding.birthDate.setOnClickListener {
            showDatePicker(state.userSettings.birthDate) { date ->
                viewModel.reduce(OptionsAction.UpdateBirthDate(date))
            }
        }

        binding.measureImperial.isChecked = state.userSettings.measureSystem == MeasureType.IMPERIAL
        binding.measureMetric.isChecked = state.userSettings.measureSystem == MeasureType.METRIC
        binding.sexFemale.isChecked = state.userSettings.sex == SexType.FEMALE
        binding.vSexMale.isChecked = state.userSettings.sex == SexType.MALE

        if (!state.hasChanged || !binding.displayName.hasFocus()) {
            binding.displayName.setText(state.userSettings.displayName)
        }

        binding.birthDate.setText(state.userSettings.birthDate.formatDate)
        binding.saveOnCloud.isChecked = state.userSettings.firebaseToken != null

        binding.userWeight.suffix = getString(state.userSettings.measureSystem.weightResId)
        binding.userHeight.suffix = getString(state.userSettings.measureSystem.heightResId)

        if (!state.hasChanged || !binding.userWeight.hasFocus()) {
            binding.userWeight.setText(
                state.userSettings.displayWeight(state.userSettings.weight)
                    .formatString()
            )
        }
        if (!state.hasChanged || !binding.userHeight.hasFocus()) {
            binding.userHeight.setText(
                state.userSettings.displayHeight(state.userSettings.height)
                    .formatString()
            )
        }

        menuEnabled = state.hasChanged
        requireActivity().invalidateOptionsMenu()
    }

    override fun onPause() {
        viewModel.reduce(OptionsAction.SaveSettings)
        super.onPause()
    }
}
package com.nmarsollier.fitfat.ui.options

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.nmarsollier.fitfat.R
import com.nmarsollier.fitfat.databinding.MainOptionsFragmentBinding
import com.nmarsollier.fitfat.model.google.GoogleRepository
import com.nmarsollier.fitfat.model.userSettings.MeasureType
import com.nmarsollier.fitfat.model.userSettings.SexType
import com.nmarsollier.fitfat.utils.closeProgressDialog
import com.nmarsollier.fitfat.utils.showDatePicker
import com.nmarsollier.fitfat.utils.showProgressDialog
import com.nmarsollier.fitfat.utils.formatDate
import com.nmarsollier.fitfat.utils.formatString
import com.nmarsollier.fitfat.utils.parseDouble
import com.nmarsollier.fitfat.utils.showToast
import com.nmarsollier.fitfat.utils.updateMenuItemColor
import kotlinx.coroutines.launch

class OptionsFragment : Fragment() {
    private val binding by lazy {
        MainOptionsFragmentBinding.inflate(layoutInflater)
    }

    private val viewModel by viewModels<OptionsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GoogleRepository.registerForLogin(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            viewModel.state.collect { state ->
                when (state) {
                    OptionsState.GoogleLoginError -> {
                        requireContext().showToast(R.string.google_error)
                    }

                    is OptionsState.Ready -> {
                        binding.birthDate.setOnClickListener {
                            showDatePicker(state.userSettings.birthDate) { date ->
                                viewModel.updateBirthDate(date)
                            }
                        }

                        binding.saveOnCloud.setOnClickListener {
                            if (state.userSettings.firebaseToken != null) {
                                disableSaveData()
                            } else {
                                loginWithGoogle()
                            }
                        }

                        refreshUI(state)
                        closeProgressDialog()
                    }

                    else -> Unit
                }
            }
        }

        binding.displayName.doOnTextChanged { text, _, _, _ ->
            viewModel.updateDisplayName(text.toString())
        }

        binding.userHeight.doOnTextChanged { text, _, _, _ ->
            viewModel.updateHeight(text.toString().parseDouble())
        }

        binding.userWeight.doOnTextChanged { text, _, _, _ ->
            viewModel.updateWeight(text.toString().parseDouble())
        }

        binding.measureImperial.setOnClickListener {
            viewModel.updateMeasureSystem(MeasureType.IMPERIAL)
        }

        binding.measureMetric.setOnClickListener {
            viewModel.updateMeasureSystem(MeasureType.METRIC)
        }

        binding.sexFemale.setOnClickListener {
            viewModel.updateSex(SexType.FEMALE)
        }

        binding.vSexMale.setOnClickListener {
            viewModel.updateSex(SexType.MALE)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.load()
    }

    private fun loginWithGoogle() {
        showProgressDialog()
        viewModel.loginWithGoogle(this)
    }

    private fun disableSaveData() {
        FirebaseAuth.getInstance().signOut()
        viewModel.disableFirebase()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_save, menu)
        menu.findItem(R.id.menu_save).isEnabled = viewModel.dataChanged

        context?.updateMenuItemColor(menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_save -> {
                viewModel.saveSettings()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun refreshUI(state: OptionsState.Ready) {
        activity?.invalidateOptionsMenu()

        binding.measureImperial.isChecked = state.userSettings.measureSystem == MeasureType.IMPERIAL
        binding.measureMetric.isChecked = state.userSettings.measureSystem == MeasureType.METRIC
        binding.sexFemale.isChecked = state.userSettings.sex == SexType.FEMALE
        binding.vSexMale.isChecked = state.userSettings.sex == SexType.MALE

        if (!state.hasChanged || !binding.displayName.hasFocus()) {
            binding.displayName.setText(state.userSettings.displayName)
        }

        binding.birthDate.setText(state.userSettings.birthDate.formatDate())
        binding.saveOnCloud.isChecked = state.userSettings.firebaseToken != null

        binding.userWeight.suffix = getString(state.userSettings.measureSystem.weightResId)
        binding.userHeight.suffix = getString(state.userSettings.measureSystem.heightResId)

        if (!state.hasChanged || !binding.userWeight.hasFocus()) {
            binding.userWeight.setText(
                state.userSettings.measureSystem.displayWeight(state.userSettings.weight)
                    .formatString()
            )
        }
        if (!state.hasChanged || !binding.userHeight.hasFocus()) {
            binding.userHeight.setText(
                state.userSettings.measureSystem.displayHeight(state.userSettings.height)
                    .formatString()
            )
        }
    }

    override fun onPause() {
        viewModel.saveSettings()
        super.onPause()
    }
}

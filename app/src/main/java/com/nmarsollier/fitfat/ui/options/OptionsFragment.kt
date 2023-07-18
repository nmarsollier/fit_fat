package com.nmarsollier.fitfat.ui.options

import android.os.Bundle
import android.view.*
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.nmarsollier.fitfat.R
import com.nmarsollier.fitfat.databinding.MainOptionsFragmentBinding
import com.nmarsollier.fitfat.model.google.GoogleLoginResult
import com.nmarsollier.fitfat.model.google.GoogleRepository
import com.nmarsollier.fitfat.model.measures.MeasuresRepository
import com.nmarsollier.fitfat.model.userSettings.MeasureType
import com.nmarsollier.fitfat.model.userSettings.SexType
import com.nmarsollier.fitfat.model.userSettings.UserSettingsRepository
import com.nmarsollier.fitfat.ui.utils.observe
import com.nmarsollier.fitfat.ui.utils.showDatePicker
import com.nmarsollier.fitfat.ui.utils.showProgressDialog
import com.nmarsollier.fitfat.utils.*
import kotlinx.coroutines.launch

class OptionsFragment : Fragment() {
    private val binding: MainOptionsFragmentBinding by lazy {
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

        viewModel.state.observe(viewModel.viewModelScope) { state ->
            when (state) {
                is OptionsState.Ready -> {
                    binding.birthDate.setOnClickListener {
                        state.userSettings.let {
                            showDatePicker(state.userSettings.birthDate) { date ->
                                viewModel.updateBirthDate(date)
                            }
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
                }
                else -> Unit
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
        viewModel.load(requireContext())
    }

    private fun loginWithGoogle() = lifecycleScope.launch {
        val closeDialog = showProgressDialog()
        val context = this@OptionsFragment.context ?: return@launch

        GoogleRepository.login(this@OptionsFragment).collect {
            when (it) {
                is GoogleLoginResult.Error -> context.showToast(R.string.google_error)
                is GoogleLoginResult.Success -> {
                    UserSettingsRepository.load(context)
                    MeasuresRepository.loadAll(context)
                }
            }
            closeDialog()
        }
    }

    private fun disableSaveData() {
        FirebaseAuth.getInstance().signOut()
        viewModel.disableFirebase()
        viewModel.saveSettings(requireContext())
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
                viewModel.saveSettings(requireContext())
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
        viewModel.saveSettings(requireContext())
        super.onPause()
    }

    companion object {
        fun newInstance() = OptionsFragment()
    }
}

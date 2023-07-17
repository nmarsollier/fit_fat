package com.nmarsollier.fitfat.ui.options

import android.os.Bundle
import android.view.*
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.firebase.auth.FirebaseAuth
import com.nmarsollier.fitfat.R
import com.nmarsollier.fitfat.databinding.MainOptionsFragmentBinding
import com.nmarsollier.fitfat.model.FirebaseDao
import com.nmarsollier.fitfat.model.MeasureType
import com.nmarsollier.fitfat.model.SexType
import com.nmarsollier.fitfat.utils.*

class OptionsFragment : Fragment() {
    private val binding: MainOptionsFragmentBinding by lazy {
        MainOptionsFragmentBinding.inflate(layoutInflater)
    }

    private val viewModel by viewModels<OptionsViewModel>()

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

        viewModel.state.observe(viewLifecycleOwner) {
            when (it) {
                is OptionsState.Ready -> refreshUI(it)
                else -> Unit
            }
        }

        binding.birthDate.setOnClickListener {
            viewModel.currentUserSettings?.let {
                showDatePicker(it.birthDate) { date ->
                    viewModel.updateBirthDate(date)
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

        binding.saveOnCloud.setOnClickListener {
            viewModel.currentUserSettings?.let { userSettings ->
                if (userSettings.firebaseToken != null) {
                    disableSaveData()
                } else {
                    FirebaseDao.login(this)
                }
            }
        }

        viewModel.load(requireContext())
    }

    /*
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val userSettings = userSettings ?: return
        val context = context ?: return

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        var dialog: Dialog? = null
        if (requestCode == ResultCodes.RC_SIGN_IN.code) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                task.getResult(ApiException::class.java)?.idToken?.let {
                    dialog = showProgressDialog(context)

                    userSettings.firebaseToken = it
                    FirebaseDao.googleAuth(it) {
                        FirebaseDao.downloadUserSettings(context, it) {
                            FirebaseDao.downloadMeasurements(context)
                            reloadSettings()
                            dialog?.dismiss()
                        }
                    }
                } ?: run {
                    dialog?.dismiss()
                }
            } catch (e: ApiException) {
                logError("Google sign in failed", e)
                dialog?.dismiss()
            }
        } else {
            dialog?.dismiss()
        }
    }*/

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

package com.nmarsollier.fitfat

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.nmarsollier.fitfat.model.*
import com.nmarsollier.fitfat.utils.*
import kotlinx.android.synthetic.main.main_options_fragment.*
import java.util.*


class MainOptions : Fragment() {
    private var userSettings: UserSettings? = null
    private var dataChanged = false
        set(value) {
            if (value != field) {
                field = value
                activity?.invalidateOptionsMenu()
            }
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val result = inflater.inflate(R.layout.main_options_fragment, container, false)
        setHasOptionsMenu(true)
        return result
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vBirthDate.setOnClickListener {
            val userSettings = userSettings ?: return@setOnClickListener

            val birthCalendar = Calendar.getInstance()
            birthCalendar.time = userSettings.birthDate

            val datePickerDialog = DatePickerDialog(
                context!!,
                DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                    val newDate = Calendar.getInstance()
                    newDate.set(year, monthOfYear, dayOfMonth)
                    userSettings.birthDate = newDate.time
                    dataChanged = true
                    refreshUI()
                },
                birthCalendar.get(Calendar.YEAR),
                birthCalendar.get(Calendar.MONTH),
                birthCalendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }

        vDisplayName.doOnTextChanged { text, _, _, _ ->
            val userSettings = userSettings ?: return@doOnTextChanged
            userSettings.displayName = text.toString()
            dataChanged = true
        }

        vHeight.doOnTextChanged { text, _, _, _ ->
            val userSettings = userSettings ?: return@doOnTextChanged
            userSettings.height = userSettings.measureSystem.standardWidth(text.toString().parseDouble())
            dataChanged = true
        }

        vWeight.doOnTextChanged { text, _, _, _ ->
            val userSettings = userSettings ?: return@doOnTextChanged
            userSettings.weight = userSettings.measureSystem.standardWeight(text.toString().parseDouble())
            dataChanged = true
        }

        vMeasureImperial.setOnClickListener {
            val userSettings = userSettings ?: return@setOnClickListener
            userSettings.measureSystem = MeasureType.IMPERIAL
            dataChanged = true
            refreshNumbers()
        }

        vMeasureMetric.setOnClickListener {
            val userSettings = userSettings ?: return@setOnClickListener
            userSettings.measureSystem = MeasureType.METRIC
            dataChanged = true
            refreshNumbers()
        }

        vSexFemale.setOnClickListener {
            val userSettings = userSettings ?: return@setOnClickListener
            userSettings.sex = SexType.FEMALE
            dataChanged = true
        }

        vSexMale.setOnClickListener {
            val userSettings = userSettings ?: return@setOnClickListener
            userSettings.sex = SexType.MALE
            dataChanged = true
        }

        vCloudSave.setOnClickListener {
            val userSettings = userSettings ?: return@setOnClickListener

            if (userSettings.firebaseToken != null) {
                disableSaveData()
            } else {
                enableSaveData()
            }
        }

    }

    private fun enableSaveData() {
        FirebaseDao.login(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val userSettings = userSettings ?: return
        val context = context ?: return

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == ResultCodes.RC_SIGN_IN.code) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                task.getResult(ApiException::class.java)?.idToken?.let {
                    val dialog = ProgressDialog.show(context, "", getString(R.string.loading), true)

                    userSettings.firebaseToken = it
                    FirebaseDao.firebaseAuthWithGoogle(it) {
                        FirebaseDao.downloadUserSettings(context, it) {
                            FirebaseDao.downloadMeasurements(context)
                            reloadSettings()
                            dialog.dismiss()
                        }
                    }
                }

            } catch (e: ApiException) {
                logError("Google sign in failed", e)
            }
        }
    }

    private fun disableSaveData() {
        FirebaseAuth.getInstance().signOut()
        val userSettings = userSettings ?: return
        userSettings.firebaseToken = null
        dataChanged = true
        saveSettings()
        refreshUI()
    }

    override fun onResume() {
        super.onResume()
        reloadSettings()
    }

    private fun reloadSettings() {
        val context = context ?: return

        runInBackground {
            userSettings = getRoomDatabase(context).userDao().getUserSettings()
            runInForeground {
                refreshUI()
                dataChanged = false
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_save, menu)
        menu.findItem(R.id.menu_save).isEnabled = dataChanged

        context?.updateMenuItemColor(menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_save -> {
                saveSettings()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun refreshUI() {
        val userSettings = userSettings ?: return

        vMeasureImperial.isChecked = userSettings.measureSystem == MeasureType.IMPERIAL
        vMeasureMetric.isChecked = userSettings.measureSystem == MeasureType.METRIC
        vSexFemale.isChecked = userSettings.sex == SexType.FEMALE
        vSexMale.isChecked = userSettings.sex == SexType.MALE
        vDisplayName.setText(userSettings.displayName)
        vBirthDate.setText(userSettings.birthDate.formatDate())
        vCloudSave.isChecked = userSettings.firebaseToken != null

        refreshNumbers()
    }

    private fun refreshNumbers() {
        val userSettings = userSettings ?: return

        vWeight.suffix = getString(userSettings.measureSystem.weightResId)
        vHeight.suffix = getString(userSettings.measureSystem.heightResId)
        vWeight.setText(userSettings.measureSystem.displayWeight(userSettings.weight).formatString())
        vHeight.setText(userSettings.measureSystem.displayHeight(userSettings.height).formatString())
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        if (!isVisibleToUser) {
            saveSettings()
        }
        super.setUserVisibleHint(isVisibleToUser)
    }

    override fun onPause() {
        saveSettings()
        super.onPause()
    }

    private fun saveSettings() {
        val context = context ?: return
        val userSettings = userSettings ?: return

        if (!dataChanged) {
            return
        }

        runInBackground {
            getRoomDatabase(context).userDao().update(userSettings)
            dataChanged = false
        }
    }

    companion object {
        fun newInstance() = MainOptions()
    }
}

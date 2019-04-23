package com.nmarsollier.fitfat

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.nmarsollier.fitfat.model.MeasureType
import com.nmarsollier.fitfat.model.SexType
import com.nmarsollier.fitfat.model.UserSettings
import com.nmarsollier.fitfat.model.getRoomDatabase
import com.nmarsollier.fitfat.utils.*
import kotlinx.android.synthetic.main.main_options_fragment.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.util.*


class MainOptions : Fragment() {
    private var userSettings: UserSettings? = null
    private var dataChanged = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.main_options_fragment, container, false)
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
            System.out.println(text)
            userSettings.displayName = text.toString()
            dataChanged = true
        }

        vHeight.doOnTextChanged { text, _, _, _ ->
            val userSettings = userSettings ?: return@doOnTextChanged
            userSettings.height = text.toStdWidth(userSettings.measureSystem)
            dataChanged = true
        }

        vWeight.doOnTextChanged { text, _, _, _ ->
            val userSettings = userSettings ?: return@doOnTextChanged
            userSettings.weight = text.toString().parseDouble().toStdWeight(userSettings.measureSystem)
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
    }

    override fun onResume() {
        super.onResume()
        reloadSettings()
    }

    private fun reloadSettings() {
        val context = context ?: return
        GlobalScope.launch {
            userSettings = getRoomDatabase(context).userDao().getUserSettings()
            dataChanged = false
            MainScope().launch {
                refreshUI()
            }
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
        refreshNumbers()
    }

    private fun refreshNumbers() {
        val userSettings = userSettings ?: return

        if (userSettings.measureSystem == MeasureType.METRIC) {
            vWeight.suffix = getString(R.string.unit_kg)
            vHeight.suffix = getString(R.string.unit_cm)
            vWeight.setText(userSettings.weight.formatString())
            vHeight.setText(userSettings.height.formatString())
        } else {
            vWeight.suffix = getString(R.string.unit_lb)
            vHeight.suffix = getString(R.string.unit_in)
            vWeight.setText(userSettings.weight.toPounds().formatString())
            vHeight.setText(userSettings.height.toInch().formatString())
        }
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

        GlobalScope.launch {
            getRoomDatabase(context).userDao().update(userSettings)
            dataChanged = false
            logInfo("User Settings Saved")
        }
    }

    companion object {
        fun newInstance() = MainOptions()
    }
}

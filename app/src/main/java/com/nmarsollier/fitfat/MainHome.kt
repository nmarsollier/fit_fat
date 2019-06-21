package com.nmarsollier.fitfat

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.nmarsollier.fitfat.model.FirebaseDao
import com.nmarsollier.fitfat.model.Measure
import com.nmarsollier.fitfat.model.UserSettings
import com.nmarsollier.fitfat.model.getRoomDatabase
import com.nmarsollier.fitfat.utils.*
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.main_home_fragment.*
import kotlinx.android.synthetic.main.main_home_measure_holder.*

class MainHome : Fragment() {
    private var userSettings: UserSettings? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.main_home_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadSettings()

        vRecyclerView.addItemDecoration(DividerItemDecoration(activity!!, DividerItemDecoration.VERTICAL))

        vFab.setOnClickListener {
            val context = context ?: return@setOnClickListener
            NewMeasureActivity.startActivity(context)
        }
    }

    private fun loadSettings() {
        val context = context ?: return
        runInBackground {
            userSettings = getRoomDatabase(context).userDao().getUserSettings()

            runInForeground {
                initAdapter()
            }
        }
    }

    private fun initAdapter() {
        userSettings?.let {
            vRecyclerView.adapter = MeasureAdapter(it, this)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (BuildConfig.DEBUG) {
            inflater.inflate(R.menu.debug_menu, menu)
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_db_inspector -> {
                dbInspector()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun dbInspector() {
        try {
            val intent = Intent()
            intent.setClassName(activity!!.packageName, "im.dino.dbinspector.activities.DbInspectorActivity")
            startActivity(intent)
        } catch (e: Exception) {
            logError("Unable to launch db inspector", e)
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisible) {
            vRecyclerView.adapter?.notifyDataSetChanged()
        }
    }

    companion object {
        fun newInstance() = MainHome()
    }


    class MeasureAdapter internal constructor(
        private val userSettings: UserSettings,
        private val fragment: Fragment
    ) :
        RecyclerView.Adapter<MeasureHolder>() {
        private var measures = emptyList<Measure>()

        init {
            getRoomDatabase(fragment.context!!).measureDao().findAll().observe(fragment,
                Observer<List<Measure>> { measures ->
                    measures?.let { values ->
                        setData(values)
                    }
                })
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeasureHolder {
            return MeasureHolder.newInstance(parent, fragment.context!!)
        }

        override fun onBindViewHolder(holder: MeasureHolder, position: Int) {
            holder.bind(userSettings, measures[position])
        }

        private fun setData(data: List<Measure>) {
            measures = data
            notifyDataSetChanged()
        }

        override fun getItemCount(): Int {
            return measures.size
        }
    }

    class MeasureHolder private constructor(itemView: View) : RecyclerView.ViewHolder(itemView), LayoutContainer {
        private lateinit var userSettings: UserSettings

        override val containerView: View?
            get() = itemView

        val context: Context = itemView.context

        fun bind(userSettings: UserSettings, measure: Measure) {
            this.userSettings = userSettings

            vDate.text = measure.date.formatDateTime()
            vMethod.setText(measure.measureMethod.labelRes)

            // Fat Percent
            val fat = measure.fatPercent
            vFatLabel.isVisible = fat > 0
            vFat.isVisible = fat > 0
            vFatSymbol.isVisible = fat > 0
            vBodyFatSepatator.isVisible = fat > 0
            vBodyFat.isVisible = fat > 0
            vBodyFatUnit.isVisible = fat > 0

            vFat.text = fat.formatString()
            vWeight.text = userSettings.measureSystem.displayWeight(measure.bodyWeight).formatString()
            vUnit.text = context.getString(userSettings.measureSystem.weightResId)
            vBodyFat.text = userSettings.measureSystem.displayWeight(measure.bodyFatMass).formatString()
            vBodyFatUnit.text = context.getString(userSettings.measureSystem.weightResId)

            // Free Fat Mass
            val freeFatMass = measure.leanWeight
            vFreeFatMassLabel.isVisible = freeFatMass > 0
            vFreeFatMass.isVisible = freeFatMass > 0
            vFreeFatMassUnit.isVisible = freeFatMass > 0
            vFreeFatMass.text = userSettings.measureSystem.displayWeight(freeFatMass).formatString()
            vFreeFatMassUnit.text = context.getString(userSettings.measureSystem.weightResId)

            // FFMI
            val freeFatMassIndex = measure.freeFatMassIndex
            vFFMILabel.isVisible = freeFatMassIndex > 0
            vFFMI.isVisible = freeFatMassIndex > 0
            vFFMI.text = freeFatMassIndex.formatString()

            itemView.setOnLongClickListener {
                AlertDialog.Builder(context)
                    .setTitle(context.getString(R.string.measure_delete_title))
                    .setMessage(context.getString(R.string.measure_delete_message))
                    .setPositiveButton(
                        android.R.string.yes
                    ) { _, _ ->
                        deleteMeasure(context, measure)
                    }
                    .setNegativeButton(
                        android.R.string.no
                    ) { _, _ ->
                    }
                    .show()
                true
            }

            itemView.setOnClickListener {
                ViewMeasureActivity.startActivity(context, measure)
            }
        }

        private fun deleteMeasure(context: Context, measure: Measure) {
            runInBackground {
                FirebaseDao.deleteMeasure(measure.uid)
                getRoomDatabase(context).measureDao().delete(measure)
            }
        }

        companion object {
            fun newInstance(parent: ViewGroup, context: Context): MeasureHolder {
                return MeasureHolder(
                    LayoutInflater.from(context).inflate(
                        R.layout.main_home_measure_holder,
                        parent,
                        false
                    )
                )
            }
        }
    }
}
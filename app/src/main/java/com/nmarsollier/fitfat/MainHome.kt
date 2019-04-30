package com.nmarsollier.fitfat

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.nmarsollier.fitfat.model.FirebaseDao
import com.nmarsollier.fitfat.model.Measure
import com.nmarsollier.fitfat.model.UserSettings
import com.nmarsollier.fitfat.model.getRoomDatabase
import com.nmarsollier.fitfat.utils.formatDateTime
import com.nmarsollier.fitfat.utils.formatString
import com.nmarsollier.fitfat.utils.runInBackground
import com.nmarsollier.fitfat.utils.runInForeground
import kotlinx.android.synthetic.main.main_home_fragment.*
import kotlinx.android.synthetic.main.main_home_measure_holder.view.*


class MainHome : Fragment() {
    private var userSettings: UserSettings? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
            getRoomDatabase(fragment.context!!).measureDao().getMeasures().observe(fragment,
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

    class MeasureHolder private constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private lateinit var userSettings: UserSettings

        fun bind(userSettings: UserSettings, measure: Measure) {
            this.userSettings = userSettings

            itemView.vDate.text = measure.date.formatDateTime()
            itemView.vMethod.setText(measure.measureMethod.labelRes)
            itemView.vFat.text = measure.fatPercent.formatString()

            itemView.vWeight.text = userSettings.measureSystem.displayWeight(measure.bodyWeight).formatString()
            itemView.vUnit.text = itemView.context.getString(userSettings.measureSystem.weightResId)

            itemView.setOnLongClickListener {
                AlertDialog.Builder(itemView.context)
                    .setTitle(itemView.context.getString(R.string.measure_delete_title))
                    .setMessage(itemView.context.getString(R.string.measure_delete_message))
                    .setPositiveButton(
                        android.R.string.yes
                    ) { _, _ ->
                        deleteMeasure(itemView.context, measure)
                    }
                    .setNegativeButton(
                        android.R.string.no
                    ) { _, _ ->
                    }
                    .show()
                true
            }

            itemView.setOnClickListener {
                ViewMeasureActivity.startActivity(itemView.context, measure)
            }
        }

        private fun deleteMeasure(context: Context, measure: Measure) {
            runInBackground {
                FirebaseDao.deleteMeasure(context, measure.uid)
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
package com.nmarsollier.fitfat.ui.dashboard

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.nmarsollier.fitfat.ui.measuresList.MeasuresListFragment
import com.nmarsollier.fitfat.ui.options.OptionsFragment
import com.nmarsollier.fitfat.ui.stats.StatsFragment

class DashboardPageAdapter(fm: FragmentActivity) : FragmentStateAdapter(fm) {
    override fun getItemCount() = Screen.entries.size

    override fun createFragment(position: Int): Fragment {
        return when (Screen.entries[position]) {
            Screen.STATS -> StatsFragment()
            Screen.OPTIONS -> OptionsFragment()
            Screen.MEASURES_LIST -> MeasuresListFragment()
        }
    }
}

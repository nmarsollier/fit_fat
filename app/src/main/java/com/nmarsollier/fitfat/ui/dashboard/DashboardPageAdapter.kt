package com.nmarsollier.fitfat.ui.dashboard

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.nmarsollier.fitfat.ui.measuresList.MeasuresListFragment
import com.nmarsollier.fitfat.ui.options.OptionsFragment
import com.nmarsollier.fitfat.ui.stats.StatsFragment

class DashboardPageAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
    override fun getCount() = Screen.entries.size

    override fun getItem(position: Int): Fragment {
        return when (Screen.entries[position]) {
            Screen.STATS -> StatsFragment()
            Screen.OPTIONS -> OptionsFragment()
            Screen.MEASURES_LIST -> MeasuresListFragment()
        }
    }
}

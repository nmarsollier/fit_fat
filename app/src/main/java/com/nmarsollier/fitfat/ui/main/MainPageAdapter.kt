package com.nmarsollier.fitfat.ui.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.nmarsollier.fitfat.ui.home.HomeFragment
import com.nmarsollier.fitfat.ui.options.OptionsFragment
import com.nmarsollier.fitfat.ui.stats.StatsFragment

class MainPageAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
    override fun getCount() = Screen.values().size

    override fun getItem(position: Int): Fragment {
        return when (Screen.values()[position]) {
            Screen.PROGRESS -> StatsFragment.newInstance()
            Screen.OPTIONS -> OptionsFragment.newInstance()
            Screen.HOME -> HomeFragment.newInstance()
        }
    }
}

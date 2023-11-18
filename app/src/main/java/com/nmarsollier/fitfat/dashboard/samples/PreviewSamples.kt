package com.nmarsollier.fitfat.dashboard.samples

import com.nmarsollier.fitfat.dashboard.ui.DashboardReducer
import com.nmarsollier.fitfat.dashboard.ui.DashboardViewModel
import com.nmarsollier.fitfat.dashboard.ui.Screen

interface DashboardViewModelSamples {
    fun reducer(): DashboardReducer
}

val DashboardViewModel.Companion.Samples: DashboardViewModelSamples
    get() = object : DashboardViewModelSamples {
        override fun reducer() = object : DashboardReducer {
            override fun setCurrentSelectedTab(screen: Screen) = Unit
        }
    }

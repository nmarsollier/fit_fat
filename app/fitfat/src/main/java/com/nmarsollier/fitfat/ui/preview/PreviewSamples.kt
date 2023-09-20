package com.nmarsollier.fitfat.ui.preview

import com.nmarsollier.fitfat.ui.dashboard.DashboardReducer
import com.nmarsollier.fitfat.ui.dashboard.DashboardViewModel
import com.nmarsollier.fitfat.ui.dashboard.Screen

interface DashboardViewModelSamples {
    fun reducer(): DashboardReducer
}

val DashboardViewModel.Companion.Samples: DashboardViewModelSamples
    get() = object : DashboardViewModelSamples {
        override fun reducer() = object : DashboardReducer {
            override fun setCurrentSelectedTab(screen: Screen) = Unit
        }
    }

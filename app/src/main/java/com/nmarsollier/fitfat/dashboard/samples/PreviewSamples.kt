package com.nmarsollier.fitfat.dashboard.samples

import com.nmarsollier.fitfat.dashboard.ui.DashboardAction
import com.nmarsollier.fitfat.dashboard.ui.DashboardViewModel

interface DashboardViewModelSamples {
    fun reduce(e: DashboardAction)
}

val DashboardViewModel.Companion.Samples: DashboardViewModelSamples
    get() = object : DashboardViewModelSamples {
        override fun reduce(e: DashboardAction) {
        }
    }

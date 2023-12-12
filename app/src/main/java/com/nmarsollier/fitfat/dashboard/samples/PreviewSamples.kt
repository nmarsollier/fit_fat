package com.nmarsollier.fitfat.dashboard.samples

import com.nmarsollier.fitfat.dashboard.ui.DashboardEvent
import com.nmarsollier.fitfat.dashboard.ui.DashboardViewModel

interface DashboardViewModelSamples {
    fun reduce(e: DashboardEvent)
}

val DashboardViewModel.Companion.Samples: DashboardViewModelSamples
    get() = object : DashboardViewModelSamples {
        override fun reduce(e: DashboardEvent) {
        }
    }

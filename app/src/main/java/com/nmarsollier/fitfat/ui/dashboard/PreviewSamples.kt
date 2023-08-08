package com.nmarsollier.fitfat.ui.dashboard

interface DashboardViewModelSamples {
    fun reduce(e: DashboardAction)
}

val DashboardViewModel.Companion.Samples: DashboardViewModelSamples
    get() = object : DashboardViewModelSamples {
        override fun reduce(e: DashboardAction) {
        }
    }

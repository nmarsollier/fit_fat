package com.nmarsollier.fitfat.dashboard.samples

import com.nmarsollier.fitfat.common.ui.viewModel.Reducer
import com.nmarsollier.fitfat.dashboard.ui.DashboardEvent
import com.nmarsollier.fitfat.dashboard.ui.DashboardView

interface DashboardViewModelSamples {
    fun reducer(): Reducer<DashboardEvent>
}

val DashboardView.Companion.Samples: DashboardViewModelSamples
    get() = object : DashboardViewModelSamples {
        override fun reducer() = object : Reducer<DashboardEvent> {
            override fun reduce(event: DashboardEvent) = Unit
        }
    }

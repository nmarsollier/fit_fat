package com.nmarsollier.fitfat.stats.samples

import com.nmarsollier.fitfat.common.ui.viewModel.Reducer
import com.nmarsollier.fitfat.stats.ui.StatsEvent
import com.nmarsollier.fitfat.stats.ui.StatsViewModel

interface StatsViewModelSamples {
    fun reducer(): Reducer<StatsEvent>
}

val StatsViewModel.Companion.Samples: StatsViewModelSamples
    get() = object : StatsViewModelSamples {
        override fun reducer() = object : Reducer<StatsEvent> {
            override fun reduce(event: StatsEvent) = Unit
        }
    }

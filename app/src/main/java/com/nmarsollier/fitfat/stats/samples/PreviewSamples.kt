package com.nmarsollier.fitfat.stats.samples

import com.nmarsollier.fitfat.stats.ui.StatsEvent
import com.nmarsollier.fitfat.stats.ui.StatsViewModel

interface StatsViewModelSamples {
    fun reduce(e: StatsEvent)
}

val StatsViewModel.Companion.Samples: StatsViewModelSamples
    get() = object : StatsViewModelSamples {
        override fun reduce(e: StatsEvent) {
        }
    }

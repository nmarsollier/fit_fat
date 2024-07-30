package com.nmarsollier.fitfat.stats.samples

import com.nmarsollier.fitfat.stats.ui.StatsAction
import com.nmarsollier.fitfat.stats.ui.StatsViewModel

interface StatsViewModelSamples {
    fun reduce(e: StatsAction)
}

val StatsViewModel.Companion.Samples: StatsViewModelSamples
    get() = object : StatsViewModelSamples {
        override fun reduce(e: StatsAction) {
        }
    }

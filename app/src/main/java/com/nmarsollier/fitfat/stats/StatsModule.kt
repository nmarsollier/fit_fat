package com.nmarsollier.fitfat.stats

import com.nmarsollier.fitfat.stats.ui.StatsView
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val koinStatsModule = module {
    viewModelOf(::StatsView)
}
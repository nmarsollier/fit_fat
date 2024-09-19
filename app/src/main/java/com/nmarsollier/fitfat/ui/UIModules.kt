package com.nmarsollier.fitfat.ui

import com.nmarsollier.fitfat.ui.dashboard.*
import com.nmarsollier.fitfat.ui.measures.edit.*
import com.nmarsollier.fitfat.ui.measures.list.*
import com.nmarsollier.fitfat.ui.stats.*
import com.nmarsollier.fitfat.ui.userSettings.*
import org.koin.androidx.viewmodel.dsl.*
import org.koin.dsl.*

val uiModule = module {
    viewModelOf(::MeasuresListViewModel)
    viewModelOf(::EditMeasureViewModel)
    viewModelOf(::StatsViewModel)
    viewModelOf(::OptionsViewModel)
    viewModelOf(::DashboardViewModel)
}

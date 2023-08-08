package com.nmarsollier.fitfat.ui

import com.nmarsollier.fitfat.ui.common.navigation.NavigationProvider
import com.nmarsollier.fitfat.ui.dashboard.DashboardViewModel
import com.nmarsollier.fitfat.ui.measures.edit.EditMeasureViewModel
import com.nmarsollier.fitfat.ui.measures.list.MeasuresListViewModel
import com.nmarsollier.fitfat.ui.stats.StatsViewModel
import com.nmarsollier.fitfat.ui.userSettings.OptionsViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val uiModule = module {
    singleOf(::NavigationProvider)

    viewModelOf(::MeasuresListViewModel)
    viewModelOf(::EditMeasureViewModel)
    viewModelOf(::StatsViewModel)
    viewModelOf(::OptionsViewModel)
    viewModelOf(::DashboardViewModel)
}

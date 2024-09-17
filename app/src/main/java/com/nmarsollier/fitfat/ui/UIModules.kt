package com.nmarsollier.fitfat.ui

import com.nmarsollier.fitfat.ui.dashboard.DashboardViewModel
import com.nmarsollier.fitfat.ui.editMeasure.EditMeasureViewModel
import com.nmarsollier.fitfat.ui.measuresList.MeasuresListViewModel
import com.nmarsollier.fitfat.ui.options.OptionsViewModel
import com.nmarsollier.fitfat.ui.stats.StatsViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val uiModule = module {

    viewModelOf(::MeasuresListViewModel)
    viewModelOf(::EditMeasureViewModel)

    viewModelOf(::StatsViewModel)

    viewModelOf(::OptionsViewModel)

    viewModelOf(::DashboardViewModel)

}

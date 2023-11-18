package com.nmarsollier.fitfat

import com.nmarsollier.fitfat.ui.dashboard.DashboardViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val dashboardModule = module {
    viewModelOf(::DashboardViewModel)
}
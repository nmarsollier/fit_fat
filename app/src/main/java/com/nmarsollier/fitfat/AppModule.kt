package com.nmarsollier.fitfat

import com.nmarsollier.fitfat.dashboard.ui.DashboardView
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val dashboardModule = module {
    viewModelOf(::DashboardView)
}
package com.nmarsollier.fitfat.common

import com.nmarsollier.fitfat.common.logger.Logger
import com.nmarsollier.fitfat.common.navigation.NavigationProvider
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val koinUtilsModule = module {
    singleOf(::Logger)
    singleOf(::NavigationProvider)
}

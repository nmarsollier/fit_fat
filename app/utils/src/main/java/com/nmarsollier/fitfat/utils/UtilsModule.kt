package com.nmarsollier.fitfat.utils

import com.nmarsollier.fitfat.utils.logger.Logger
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val koinUtilsModule = module {
    singleOf(::Logger)
}
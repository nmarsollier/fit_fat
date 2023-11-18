package com.nmarsollier.fitfat.common

import com.nmarsollier.fitfat.common.logger.Logger
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val koinUtilsModule = module {
    singleOf(::Logger)
}
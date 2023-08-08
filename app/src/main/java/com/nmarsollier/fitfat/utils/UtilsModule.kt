package com.nmarsollier.fitfat.utils

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val koinUtilsModule = module {
    singleOf(::Logger)
}

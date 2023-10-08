package com.nmarsollier.fitfat.useCases

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val koinUseCaseModules = module {
    factoryOf(::FirebaseLoginUseCase)
    factoryOf(::FirebaseMeasuresSyncUseCase)
    factoryOf(::FirebaseSettingsSyncUseCase)
}

package com.nmarsollier.fitfat.firebase

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val koinFirebaseModule = module {
    singleOf(::FirebaseConnection)
    singleOf(::GoogleAuthService)
}

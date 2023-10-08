package com.nmarsollier.fitfat.model

import com.nmarsollier.fitfat.model.db.getRoomDatabase
import com.nmarsollier.fitfat.model.firebase.FirebaseConnection
import com.nmarsollier.fitfat.model.firebase.GoogleAuth
import com.nmarsollier.fitfat.model.firebase.MeasuresFirebaseRepository
import com.nmarsollier.fitfat.model.firebase.UserSettingsFirebaseRepository
import com.nmarsollier.fitfat.model.measures.MeasuresRepository
import com.nmarsollier.fitfat.model.userSettings.UserSettingsRepository
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val koinDatabaseModule = module {
    single { getRoomDatabase(androidContext()) }

    singleOf(::MeasuresRepository)
    singleOf(::UserSettingsRepository)
    singleOf(::FirebaseConnection)
    singleOf(::GoogleAuth)
    singleOf(::MeasuresFirebaseRepository)
    singleOf(::UserSettingsFirebaseRepository)
}

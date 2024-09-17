package com.nmarsollier.fitfat.models

import com.nmarsollier.fitfat.common.utils.Logger
import com.nmarsollier.fitfat.models.firebase.FirebaseConnection
import com.nmarsollier.fitfat.models.firebase.GoogleAuthService
import com.nmarsollier.fitfat.models.measures.DownloadMeasuresFirebaseService
import com.nmarsollier.fitfat.models.measures.MeasuresRepository
import com.nmarsollier.fitfat.models.measures.SaveMeasureAndUserSettingsService
import com.nmarsollier.fitfat.models.measures.UploadMeasuresFirebaseService
import com.nmarsollier.fitfat.models.measures.api.MeasuresFirebaseApi
import com.nmarsollier.fitfat.models.measures.db.MeasuresDatabase
import com.nmarsollier.fitfat.models.measures.db.measuresDatabase
import com.nmarsollier.fitfat.models.userSettings.DownloadSyncFirebaseService
import com.nmarsollier.fitfat.models.userSettings.UploadSyncFirebaseService
import com.nmarsollier.fitfat.models.userSettings.UserSettingsRepository
import com.nmarsollier.fitfat.models.userSettings.api.UserSettingsFirebaseApi
import com.nmarsollier.fitfat.models.userSettings.db.UserSettingsDatabase
import com.nmarsollier.fitfat.models.userSettings.db.userSettingsDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.createdAtStart
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.withOptions
import org.koin.dsl.module

val modelsModule = module {
    singleOf(::FirebaseConnection)
    singleOf(::GoogleAuthService)

    // Measures
    single { measuresDatabase(androidContext()) }

    factory {
        val database: MeasuresDatabase = get()
        database.measureDao()
    }

    factoryOf(::SaveMeasureAndUserSettingsService)
    singleOf(::MeasuresRepository)
    factoryOf(::MeasuresFirebaseApi)
    singleOf(::DownloadMeasuresFirebaseService) withOptions { createdAtStart() }
    singleOf(::UploadMeasuresFirebaseService) withOptions { createdAtStart() }

    // User settings
    single { userSettingsDatabase(androidContext()) }

    factory {
        val db: UserSettingsDatabase = get()
        db.userDao()
    }

    factoryOf(::UploadSyncFirebaseService)
    singleOf(::DownloadSyncFirebaseService) withOptions { createdAtStart() }
    singleOf(::UserSettingsRepository)
    factoryOf(::UserSettingsFirebaseApi)

    singleOf(::Logger)
}

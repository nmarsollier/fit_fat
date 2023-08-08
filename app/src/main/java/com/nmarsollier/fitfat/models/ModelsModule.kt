package com.nmarsollier.fitfat.models

import com.nmarsollier.fitfat.models.firebase.*
import com.nmarsollier.fitfat.models.measures.*
import com.nmarsollier.fitfat.models.measures.api.*
import com.nmarsollier.fitfat.models.measures.db.*
import com.nmarsollier.fitfat.models.userSettings.*
import com.nmarsollier.fitfat.models.userSettings.api.*
import com.nmarsollier.fitfat.models.userSettings.db.*
import org.koin.android.ext.koin.*
import org.koin.core.module.dsl.*
import org.koin.dsl.*

val modelsModule = module {
    singleOf(::FirebaseConnection)
    singleOf(::GoogleAuthService)

    // Measures
    single { measuresDatabase(androidContext()) }

    factory {
        val database: MeasuresDatabase = get()
        database.measureDao()
    }

    singleOf(::SaveMeasureAndUserSettingsService)
    singleOf(::MeasuresRepository)
    singleOf(::MeasuresFirebaseApi)
    singleOf(::DownloadMeasuresFirebaseService) withOptions { createdAtStart() }
    singleOf(::UploadMeasuresFirebaseService) withOptions { createdAtStart() }

    // User settings
    single { userSettingsDatabase(androidContext()) }

    factory {
        val db: UserSettingsDatabase = get()
        db.userDao()
    }

    singleOf(::UploadSyncFirebaseService)
    singleOf(::DownloadSyncFirebaseService) withOptions { createdAtStart() }
    singleOf(::UserSettingsRepository)
    singleOf(::UserSettingsFirebaseApi)
}

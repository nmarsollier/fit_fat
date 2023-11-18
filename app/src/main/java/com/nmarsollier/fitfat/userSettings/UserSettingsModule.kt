package com.nmarsollier.fitfat.userSettings

import com.nmarsollier.fitfat.userSettings.model.DownloadSyncFirebaseService
import com.nmarsollier.fitfat.userSettings.model.UploadSyncFirebaseService
import com.nmarsollier.fitfat.userSettings.model.db.UserSettingsDatabase
import com.nmarsollier.fitfat.userSettings.model.db.getRoomDatabase
import com.nmarsollier.fitfat.userSettings.model.UserSettingsRepository
import com.nmarsollier.fitfat.userSettings.model.api.UserSettingsFirebaseApi
import com.nmarsollier.fitfat.userSettings.ui.OptionsView
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.createdAtStart
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.withOptions
import org.koin.dsl.module

val userSettingsModule = module {

    single { getRoomDatabase(androidContext()) }

    factory {
        val db: UserSettingsDatabase = get()
        db.userDao()
    }

    factoryOf(::UploadSyncFirebaseService)
    singleOf(::DownloadSyncFirebaseService) withOptions { createdAtStart() }
    factoryOf(::UserSettingsRepository)
    factoryOf(::UserSettingsFirebaseApi)

    viewModelOf(::OptionsView)
}

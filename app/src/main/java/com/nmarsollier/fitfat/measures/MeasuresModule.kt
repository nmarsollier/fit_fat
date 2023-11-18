package com.nmarsollier.fitfat.measures

import com.nmarsollier.fitfat.measures.model.DownloadMeasuresFirebaseService
import com.nmarsollier.fitfat.measures.model.MeasuresRepository
import com.nmarsollier.fitfat.measures.model.SaveMeasureAndUserSettingsService
import com.nmarsollier.fitfat.measures.model.UploadMeasuresFirebaseService
import com.nmarsollier.fitfat.measures.model.api.MeasuresFirebaseApi
import com.nmarsollier.fitfat.measures.model.db.MeasuresDatabase
import com.nmarsollier.fitfat.measures.model.db.getRoomDatabase
import com.nmarsollier.fitfat.measures.ui.edit.EditMeasureView
import com.nmarsollier.fitfat.measures.ui.list.MeasuresListView
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.createdAtStart
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.withOptions
import org.koin.dsl.module

val measuresModule = module {
    single { getRoomDatabase(androidContext()) }

    factory {
        val database: MeasuresDatabase = get()
        database.measureDao()
    }

    factoryOf(::SaveMeasureAndUserSettingsService)
    factoryOf(::MeasuresRepository)
    factoryOf(::MeasuresFirebaseApi)
    singleOf(::DownloadMeasuresFirebaseService) withOptions { createdAtStart() }
    singleOf(::UploadMeasuresFirebaseService) withOptions { createdAtStart() }

    viewModelOf(::MeasuresListView)
    viewModelOf(::EditMeasureView)
}

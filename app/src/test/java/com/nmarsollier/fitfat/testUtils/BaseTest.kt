package com.nmarsollier.fitfat.testUtils

import com.nmarsollier.fitfat.models.firebase.*
import com.nmarsollier.fitfat.models.measures.*
import com.nmarsollier.fitfat.models.measures.api.*
import com.nmarsollier.fitfat.models.measures.db.*
import com.nmarsollier.fitfat.models.userSettings.*
import com.nmarsollier.fitfat.models.userSettings.api.*
import com.nmarsollier.fitfat.models.userSettings.db.*
import com.nmarsollier.fitfat.ui.measures.*
import com.nmarsollier.fitfat.ui.measures.edit.*
import com.nmarsollier.fitfat.ui.measures.list.*
import com.nmarsollier.fitfat.ui.userSettings.*
import io.mockk.*
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.*
import org.koin.androidx.viewmodel.dsl.*
import org.koin.core.module.dsl.*
import org.koin.dsl.*
import org.koin.test.*

@ExperimentalCoroutinesApi
open class BaseTest : KoinTest {
    val userDaoMock = mockk<UserSettingsDao_Impl>(relaxed = true) {
        coEvery { findCurrent() } answers {
            UserSettings.Samples.simpleData.asEntity
        }
    }

    val measureDaoMock = mockk<MeasureDao_Impl>(relaxed = true) {
        coEvery { findAll() } returns (Measure.Samples.simpleData.map { it.asMeasureEntity })
        coEvery { findLast() } returns (Measure.Samples.simpleData.last().asMeasureEntity)
        coEvery { findUnSynced() } returns (Measure.Samples.simpleData.map { it.asMeasureEntity })
        coEvery { findById(any()) } returns (Measure.Samples.simpleData.first().asMeasureEntity)
    }

    val firebaseRepositoryMock = mockk<MeasuresFirebaseApi>(relaxed = true) {
        coEvery { findAll(any()) } returns (Measure.Samples.simpleData)
        coEvery { update(any()) } returns (mockk(relaxed = true) {
            every { isSuccessful } returns (true)
        })
    }

    val firebaseConnectionMock = mockk<FirebaseConnection>(relaxed = true) {

    }

    val modelsModule = module {
        factory<UserSettingsFirebaseApi> { mockk(relaxed = true) }

        factory<UserSettingsDao> { userDaoMock }

        factory<MeasureDao> { measureDaoMock }
        factoryOf(::UserSettingsRepository)
        factoryOf(::UploadSyncFirebaseService)
        factoryOf(::SaveMeasureAndUserSettingsService)
        factoryOf(::MeasuresRepository)
        factory { firebaseRepositoryMock }
        factoryOf(::UploadMeasuresFirebaseService)

        factory { firebaseConnectionMock }

        viewModelOf(::MeasuresListViewModel)
        viewModelOf(::EditMeasureViewModel)
    }

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        // Your KoinApplication instance here
        modules(
            modelsModule
        )
    }

    init {
        Dispatchers.setMain(Dispatchers.Default)
        Dispatchers.setMain(Dispatchers.Unconfined)
    }
}
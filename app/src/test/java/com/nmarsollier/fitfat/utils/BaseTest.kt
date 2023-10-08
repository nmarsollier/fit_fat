package com.nmarsollier.fitfat.utils

import com.nmarsollier.fitfat.model.db.FitFatDatabase
import com.nmarsollier.fitfat.model.firebase.FirebaseConnection
import com.nmarsollier.fitfat.model.firebase.MeasuresFirebaseRepository
import com.nmarsollier.fitfat.model.firebase.GoogleAuth
import com.nmarsollier.fitfat.model.measures.Measure
import com.nmarsollier.fitfat.model.measures.MeasureDao
import com.nmarsollier.fitfat.model.measures.MeasuresRepository
import com.nmarsollier.fitfat.model.userSettings.UserSettingsEntity
import com.nmarsollier.fitfat.model.userSettings.UserSettingsDao
import com.nmarsollier.fitfat.model.userSettings.UserSettingsRepository
import com.nmarsollier.fitfat.ui.koinUiModule
import com.nmarsollier.fitfat.useCases.koinUseCaseModules
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.setMain
import org.junit.Rule
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule

@ExperimentalCoroutinesApi
open class BaseTest : KoinTest {
    val userDaoMock = mockk<UserSettingsDao>(relaxed = true) {
        every { findCurrent() } answers {
            UserSettingsEntity.Samples.simpleData
        }
    }

    val measureDaoMock = mockk<MeasureDao>(relaxed = true) {
        every { findAll() } returns (Measure.Samples.simpleData)
        every { findLast() } returns (Measure.Samples.simpleData.last())
        every { findUnSynced() } returns (Measure.Samples.simpleData)
        every { findById(any()) } returns (Measure.Samples.simpleData.first())
    }


    val koinTestModule = module {
        single<FitFatDatabase> {
            mockk {
                every { userDao() } returns (userDaoMock)
                every { measureDao() } returns (measureDaoMock)
            }
        }

        singleOf(::MeasuresRepository)
        singleOf(::UserSettingsRepository)
        singleOf(::FirebaseConnection)
        singleOf(::GoogleAuth)
        singleOf(::MeasuresFirebaseRepository)
    }

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        // Your KoinApplication instance here
        modules(koinTestModule, koinUseCaseModules, koinUiModule)
    }

    init {
        Dispatchers.setMain(Dispatchers.Default)
        Dispatchers.setMain(Dispatchers.Unconfined)
    }
}
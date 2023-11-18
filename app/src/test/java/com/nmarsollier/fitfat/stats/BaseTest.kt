package com.nmarsollier.fitfat.stats

import com.nmarsollier.fitfat.measures.model.Measure
import com.nmarsollier.fitfat.measures.model.MeasuresRepository
import com.nmarsollier.fitfat.measures.samples.Samples
import com.nmarsollier.fitfat.stats.ui.StatsViewModel
import com.nmarsollier.fitfat.userSettings.samples.Samples
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.setMain
import org.junit.Rule
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule

@ExperimentalCoroutinesApi
open class BaseTest : KoinTest {

    val userRepositoryMock =
        mockk<com.nmarsollier.fitfat.userSettings.model.UserSettingsRepository>(relaxed = true) {
            coEvery { findCurrent() } answers {
                com.nmarsollier.fitfat.userSettings.model.UserSettings.Samples.simpleData
            }
        }

    val measureRepositoryMock =
        mockk<MeasuresRepository>(relaxed = true) {
            coEvery { findAll() } returns (Measure.Samples.simpleData)
            coEvery { findLast() } returns (Measure.Samples.simpleData.last())
            coEvery { findUnSynced() } returns (Measure.Samples.simpleData)
            coEvery { findById(any()) } returns (Measure.Samples.simpleData.first())
        }


    val measuresTestModule = module {
        factory { userRepositoryMock }
        factory { measureRepositoryMock }
        viewModelOf(::StatsViewModel)
    }

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        // Your KoinApplication instance here
        modules(
            measuresTestModule
        )
    }

    init {
        Dispatchers.setMain(Dispatchers.Default)
        Dispatchers.setMain(Dispatchers.Unconfined)
    }
}
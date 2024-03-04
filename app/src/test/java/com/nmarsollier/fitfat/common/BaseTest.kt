package com.nmarsollier.fitfat.common

import com.nmarsollier.fitfat.applicationModule
import com.nmarsollier.fitfat.userSettings.model.UserSettings
import com.nmarsollier.fitfat.userSettings.model.UserSettingsRepository
import com.nmarsollier.fitfat.userSettings.samples.Samples
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.setMain
import org.junit.Rule
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule

@ExperimentalCoroutinesApi
open class BaseTest : KoinTest {
    val userSettingsRepository =
        mockk<UserSettingsRepository>(relaxed = true) {
            coEvery { findCurrent() } answers {
                UserSettings.Samples.simpleData
            }
        }


    val dashboardTestModule = module {
        factory { userSettingsRepository }
    }


    @get:Rule
    val koinTestRule = KoinTestRule.create {
        // Your KoinApplication instance here
        modules(
            applicationModule, dashboardTestModule
        )
    }

    init {
        Dispatchers.setMain(Dispatchers.Default)
        Dispatchers.setMain(Dispatchers.Unconfined)
    }
}
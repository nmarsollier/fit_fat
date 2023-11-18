package com.nmarsollier.fitfat

import app.cash.turbine.test
import com.nmarsollier.fitfat.ui.dashboard.DashboardState
import com.nmarsollier.fitfat.ui.dashboard.DashboardViewModel
import com.nmarsollier.fitfat.ui.dashboard.Screen
import com.nmarsollier.fitfat.userSettings.model.UserSettings
import com.nmarsollier.fitfat.userSettings.ui.utils.preview.Samples
import com.nmarsollier.fitfat.utils.BaseTest
import io.mockk.coEvery
import io.mockk.coVerify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.koin.test.get

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModelTest : BaseTest() {
    @Test
    fun testExistingInitialTab(): Unit = runBlocking {
        DashboardViewModel(get()).apply {
            state.test {
                assertEquals(DashboardState.Loading(Screen.MEASURES_LIST), awaitItem())
                init()

                assertEquals(DashboardState.Ready(Screen.MEASURES_LIST), awaitItem())

                coVerify { userSettingsRepository.findCurrent() }
            }
        }
    }

    @Test
    fun testNewInitialTab(): Unit = runBlocking {
        coEvery { userSettingsRepository.findCurrent() } answers {
            UserSettings.Samples.simpleData
        }

        DashboardViewModel(get()).apply {
            state.test {
                assertEquals(DashboardState.Loading(Screen.MEASURES_LIST), awaitItem())
                init()

                assertEquals(DashboardState.Ready(Screen.MEASURES_LIST), awaitItem())

                coVerify { userSettingsRepository.findCurrent() }
            }
        }
    }

    @Test
    fun testTabSwitch(): Unit = runBlocking {
        DashboardViewModel(get()).apply {
            state.test {
                assertEquals(DashboardState.Loading(Screen.MEASURES_LIST), awaitItem())
                init()

                assertEquals(DashboardState.Ready(Screen.MEASURES_LIST), awaitItem())

                setCurrentSelectedTab(Screen.OPTIONS)
                assertEquals(DashboardState.Ready(Screen.OPTIONS), awaitItem())

                setCurrentSelectedTab(Screen.STATS)
                assertEquals(DashboardState.Ready(Screen.STATS), awaitItem())

                setCurrentSelectedTab(Screen.MEASURES_LIST)
                assertEquals(DashboardState.Ready(Screen.MEASURES_LIST), awaitItem())
            }
        }
    }
}
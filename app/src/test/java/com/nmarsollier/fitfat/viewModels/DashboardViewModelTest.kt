package com.nmarsollier.fitfat.viewModels

import app.cash.turbine.*
import com.nmarsollier.fitfat.models.userSettings.*
import com.nmarsollier.fitfat.testUtils.*
import com.nmarsollier.fitfat.ui.dashboard.*
import com.nmarsollier.fitfat.ui.userSettings.*
import io.mockk.*
import kotlinx.coroutines.*
import org.junit.Test
import org.koin.test.*
import kotlin.test.*

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
                init()

                assertEquals(DashboardState(Screen.MEASURES_LIST), awaitItem())

                coVerify { userDaoMock.findCurrent() }
            }
        }
    }

    @Test
    fun testNewInitialTab(): Unit = runBlocking {
        coEvery { userDaoMock.findCurrent() } answers {
            UserSettings.Samples.simpleData.asEntity
        }

        DashboardViewModel(get()).apply {
            state.test {
                assertEquals(DashboardState(Screen.MEASURES_LIST), awaitItem())

                coVerify { userDaoMock.findCurrent() }
            }
        }
    }

    @Test
    fun testTabSwitch(): Unit = runBlocking {
        DashboardViewModel(get()).apply {
            state.test {
                assertEquals(DashboardState(Screen.MEASURES_LIST), awaitItem())

                setCurrentSelectedTab(DashboardAction.CurrentSelectedTab(Screen.OPTIONS))
                assertEquals(DashboardState(Screen.OPTIONS), awaitItem())

                setCurrentSelectedTab(DashboardAction.CurrentSelectedTab(Screen.STATS))
                assertEquals(DashboardState(Screen.STATS), awaitItem())

                setCurrentSelectedTab(DashboardAction.CurrentSelectedTab(Screen.MEASURES_LIST))
                assertEquals(DashboardState(Screen.MEASURES_LIST), awaitItem())
            }
        }
    }
}
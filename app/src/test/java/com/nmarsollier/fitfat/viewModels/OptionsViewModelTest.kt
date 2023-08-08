package com.nmarsollier.fitfat.viewModels

import app.cash.turbine.*
import com.nmarsollier.fitfat.models.userSettings.*
import com.nmarsollier.fitfat.testUtils.*
import com.nmarsollier.fitfat.ui.userSettings.*
import io.mockk.*
import kotlinx.coroutines.*
import org.junit.*
import org.junit.Assert.assertEquals
import org.koin.test.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class OptionsViewModelTest : BaseTest() {
    @Test
    fun loadTest(): Unit = runBlocking {
        OptionsViewModel(get(), get(), get()).apply {
            coEvery { userDaoMock.findCurrent() }.returns(UserSettings.Samples.simpleData.asEntity)

            state.test {
                assertEquals(
                    OptionsState.Ready(
                        UserSettings.Samples.simpleData, false
                    ), awaitItem()
                )

                coVerify { userDaoMock.findCurrent() }
            }
        }
    }
}
package com.nmarsollier.fitfat.userSettings

import app.cash.turbine.test
import com.nmarsollier.fitfat.userSettings.model.UserSettings
import com.nmarsollier.fitfat.userSettings.ui.options.OptionsState
import com.nmarsollier.fitfat.userSettings.ui.options.OptionsViewModel
import com.nmarsollier.fitfat.userSettings.ui.utils.preview.Samples
import io.mockk.verify
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
class OptionsViewModelTest : BaseTest() {
    @Test
    fun loadTest(): Unit = runBlocking {
        com.nmarsollier.fitfat.userSettings.ui.options.OptionsViewModel(get(), get(), get()).apply {
            state.test {
                assertEquals(com.nmarsollier.fitfat.userSettings.ui.options.OptionsState.Loading, awaitItem())

                load()
                assertEquals(
                    com.nmarsollier.fitfat.userSettings.ui.options.OptionsState.Ready(
                        com.nmarsollier.fitfat.userSettings.model.UserSettings.Samples.simpleData.value,
                        false
                    ), awaitItem()
                )

                verify { userDaoMock.findCurrent() }
            }
        }
    }
}
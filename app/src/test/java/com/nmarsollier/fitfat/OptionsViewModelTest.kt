package com.nmarsollier.fitfat

import app.cash.turbine.test
import com.nmarsollier.fitfat.model.userSettings.UserSettingsEntity
import com.nmarsollier.fitfat.ui.options.OptionsState
import com.nmarsollier.fitfat.ui.options.OptionsViewModel
import com.nmarsollier.fitfat.utils.BaseTest
import com.nmarsollier.fitfat.utils.Samples
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
        OptionsViewModel(get(), get(), get()).apply {
            state.test {
                assertEquals(OptionsState.Loading, awaitItem())

                load()
                assertEquals(
                    OptionsState.Ready(
                        UserSettingsEntity.Samples.simpleData,
                        false
                    ), awaitItem()
                )

                verify { userDaoMock.findCurrent() }
            }
        }
    }
}
package com.nmarsollier.fitfat.measures

import app.cash.turbine.test
import com.nmarsollier.fitfat.measures.model.Measure
import com.nmarsollier.fitfat.measures.ui.measuresList.Destination
import com.nmarsollier.fitfat.measures.ui.measuresList.MeasuresListState
import com.nmarsollier.fitfat.measures.ui.measuresList.MeasuresListViewModel
import com.nmarsollier.fitfat.measures.ui.utils.preview.Samples
import com.nmarsollier.fitfat.userSettings.model.UserSettings
import com.nmarsollier.fitfat.userSettings.ui.utils.preview.Samples
import io.mockk.coVerify
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.koin.test.get
import kotlin.test.assertEquals

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MeasuresListViewModelTest : BaseTest() {

    @Test
    fun loadTest(): Unit = runTest {
        MeasuresListViewModel(get(), get()).apply {
            state.test {
                assertEquals(MeasuresListState.Loading, awaitItem())

                load()
                assertEquals(
                    MeasuresListState.Ready(
                        UserSettings.Samples.simpleData.value,
                        Measure.Samples.simpleData.map { it.value }
                    ), awaitItem()
                )

                coVerify { userSettingsRepository.findCurrent() }
                verify { measureDaoMock.findAll() }
            }
        }
    }

    @Test
    fun openNewMeasureTest(): Unit = runBlocking {
        MeasuresListViewModel(get(), get()).apply {
            state.test {
                assertEquals(MeasuresListState.Loading, awaitItem())

                openNewMeasure()
                assertEquals(
                    MeasuresListState.Redirect(Destination.NewMeasure), awaitItem()
                )
            }
        }
    }

    @Test
    fun openViewMeasureTest(): Unit = runBlocking {
        MeasuresListViewModel(get(), get()).apply {
            state.test {
                assertEquals(MeasuresListState.Loading, awaitItem())

                openViewMeasure(Measure.Samples.simpleData.first().value)
                assertEquals(
                    MeasuresListState.Redirect(
                        Destination.ViewMeasure(
                            Measure.Samples.simpleData.first().value
                        )
                    ), awaitItem()
                )
            }
        }
    }

    @Test
    fun deleteTest(): Unit = runBlocking {
        MeasuresListViewModel(get(), get()).apply {
            state.test {
                assertEquals(MeasuresListState.Loading, awaitItem())

                deleteMeasure(Measure.Samples.simpleData.first().value)
                assertEquals(
                    MeasuresListState.Ready(
                        UserSettings.Samples.simpleData.value,
                        Measure.Samples.simpleData.map { it.value }
                    ), awaitItem()
                )

                coVerify { userSettingsRepository.findCurrent() }
                verify { measureDaoMock.findAll() }
                verify { measureDaoMock.delete(any()) }
            }
        }
    }
}
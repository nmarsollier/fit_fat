package com.nmarsollier.fitfat.viewModels

import app.cash.turbine.*
import com.nmarsollier.fitfat.models.measures.*
import com.nmarsollier.fitfat.models.userSettings.*
import com.nmarsollier.fitfat.testUtils.*
import com.nmarsollier.fitfat.ui.measures.*
import com.nmarsollier.fitfat.ui.measures.list.*
import com.nmarsollier.fitfat.ui.userSettings.*
import io.mockk.*
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.Test
import org.koin.test.*
import kotlin.test.*

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

                assertEquals(
                    MeasuresListState.Ready(
                        UserSettings.Samples.simpleData, Measure.Samples.simpleData
                    ), awaitItem()
                )

                coVerify { userDaoMock.findCurrent() }
                coVerify { measureDaoMock.findAll() }
            }
        }
    }

    @Test
    fun openNewMeasureTest(): Unit = runBlocking {
        MeasuresListViewModel(get(), get()).apply {
            state.test {
                assertEquals(MeasuresListState.Loading, awaitItem())
                assertEquals(
                    MeasuresListState.Ready(
                        UserSettings.Samples.simpleData, Measure.Samples.simpleData
                    ), awaitItem()
                )
            }
            event.test {
                reduce(MeasuresListAction.OpenNewMeasure)

                assertEquals(
                    MeasuresListEvent.Redirect(Destination.NewMeasure), awaitItem()
                )
            }
        }
    }

    @Test
    fun openViewMeasureTest(): Unit = runBlocking {
        MeasuresListViewModel(get(), get()).apply {
            state.test {
                assertEquals(MeasuresListState.Loading, awaitItem())
                assertEquals(
                    MeasuresListState.Ready(
                        UserSettings.Samples.simpleData, Measure.Samples.simpleData
                    ), awaitItem()
                )
            }
            event.test {
                reduce(MeasuresListAction.OpenViewMeasure(Measure.Samples.simpleData.first()))

                assertEquals(
                    MeasuresListEvent.Redirect(
                        Destination.ViewMeasure(
                            Measure.Samples.simpleData.first()
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
                assertEquals(
                    MeasuresListState.Ready(
                        UserSettings.Samples.simpleData, Measure.Samples.simpleData
                    ), awaitItem()
                )

                reduce(MeasuresListAction.DeleteMeasure(Measure.Samples.simpleData.first()))

                assertEquals(MeasuresListState.Loading, awaitItem())
                assertEquals(
                    MeasuresListState.Ready(
                        UserSettings.Samples.simpleData, Measure.Samples.simpleData
                    ), awaitItem()
                )

                coVerify { userDaoMock.findCurrent() }
                coVerify { measureDaoMock.findAll() }
                coVerify { measureDaoMock.delete(any()) }
            }
        }
    }
}
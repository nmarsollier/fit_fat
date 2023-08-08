package com.nmarsollier.fitfat

import app.cash.turbine.test
import com.nmarsollier.fitfat.model.measures.Measure
import com.nmarsollier.fitfat.model.userSettings.UserSettingsEntity
import com.nmarsollier.fitfat.ui.measuresList.Destination
import com.nmarsollier.fitfat.ui.measuresList.MeasuresListState
import com.nmarsollier.fitfat.ui.measuresList.MeasuresListViewModel
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
class MeasuresListViewModelTest : BaseTest() {
    @Test
    fun loadTest(): Unit = runBlocking {
        MeasuresListViewModel(get(), get()).apply {
            state.test {
                assertEquals(MeasuresListState.Loading, awaitItem())

                load()
                assertEquals(
                    MeasuresListState.Ready(
                        UserSettingsEntity.Samples.simpleData,
                        Measure.Samples.simpleData
                    ), awaitItem()
                )

                verify { userDaoMock.findCurrent() }
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

                openViewMeasure(Measure.Samples.simpleData.first())
                assertEquals(
                    MeasuresListState.Redirect(
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

                deleteMeasure(Measure.Samples.simpleData.first())
                assertEquals(
                    MeasuresListState.Ready(
                        UserSettingsEntity.Samples.simpleData,
                        Measure.Samples.simpleData
                    ), awaitItem()
                )

                verify { userDaoMock.findCurrent() }
                verify { measureDaoMock.findAll() }
                verify { measureDaoMock.delete(any()) }
            }
        }
    }
}
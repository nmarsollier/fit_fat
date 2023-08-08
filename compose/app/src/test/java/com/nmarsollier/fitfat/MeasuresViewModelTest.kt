package com.nmarsollier.fitfat

import app.cash.turbine.test
import com.nmarsollier.fitfat.model.measures.Measure
import com.nmarsollier.fitfat.model.measures.MeasureMethod
import com.nmarsollier.fitfat.model.measures.MeasureValue
import com.nmarsollier.fitfat.model.userSettings.UserSettingsEntity
import com.nmarsollier.fitfat.ui.editMeasure.EditMeasureState
import com.nmarsollier.fitfat.ui.editMeasure.EditMeasureViewModel
import com.nmarsollier.fitfat.utils.BaseTest
import com.nmarsollier.fitfat.utils.Samples
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.koin.test.get
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MeasuresViewModelTest : BaseTest() {
    @Test
    fun loadNewTest(): Unit = runBlocking {
        EditMeasureViewModel(get(), get()).apply {
            state.test {
                assertEquals(EditMeasureState.Loading(null, false), awaitItem())

                init(null)
                val state = awaitItem()

                assertTrue(state is EditMeasureState.Ready)
                assertEquals(UserSettingsEntity.Samples.simpleData, state.userSettingsEntity)
                assertNotNull(state.measure)
                assertFalse(state.readOnly)

                verify { userDaoMock.findCurrent() }
                verify { measureDaoMock.findLast() }
            }
        }
    }

    @Test
    fun loadExistingTest(): Unit = runBlocking {
        EditMeasureViewModel(get(), get()).apply {
            state.test {
                assertEquals(EditMeasureState.Loading(null, false), awaitItem())

                init(Measure.Samples.bodyFat)
                assertEquals(
                    EditMeasureState.Loading(
                        Measure.Samples.bodyFat, true
                    ), awaitItem()
                )

                assertEquals(
                    EditMeasureState.Ready(
                        UserSettingsEntity.Samples.simpleData, Measure.Samples.bodyFat, null, false, true
                    ), awaitItem()
                )

                verify { userDaoMock.findCurrent() }
                verify { measureDaoMock.findLast() }
            }
        }
    }

    @Test
    fun closeTest(): Unit = runBlocking {
        EditMeasureViewModel(get(), get()).apply {
            state.test {
                assertEquals(EditMeasureState.Loading(null, false), awaitItem())

                close()
                assertEquals(
                    EditMeasureState.Close, awaitItem()
                )
            }
        }
    }

    @Test
    fun updateMeasureValueTest(): Unit = runBlocking {
        var initialState = EditMeasureState.Ready(
            UserSettingsEntity.Samples.simpleData, Measure.Samples.simpleData.last(), null, false, false
        )

        EditMeasureViewModel(get(), get()).apply {
            state.test {
                assertEquals(EditMeasureState.Loading(null, false), awaitItem())
                init(null)

                val state = awaitItem()
                initialState = initialState.copy(
                    measure = initialState.measure.copy(
                        uid = (state as EditMeasureState.Ready).measure.uid,
                        date = (state as EditMeasureState.Ready).measure.date,
                    )
                )
                assertEquals(
                    initialState, state
                )

                updateMeasureValue(MeasureValue.BODY_WEIGHT, 180)
                assertEquals(
                    initialState.copy(
                        measure = initialState.measure.copy(
                            bodyWeight = 180.0,
                            fatPercent = 0.0
                        )
                    ), awaitItem()
                )
            }
        }
    }

    @Test
    fun updateMeasureMethodTest(): Unit = runBlocking {
        var initialState = EditMeasureState.Ready(
            UserSettingsEntity.Samples.simpleData, Measure.Samples.simpleData.last(), null, false, false
        )

        EditMeasureViewModel(get(), get()).apply {
            state.test {
                assertEquals(EditMeasureState.Loading(null, false), awaitItem())

                init(null)
                val state = awaitItem()
                initialState = initialState.copy(
                    measure = initialState.measure.copy(
                        uid = (state as EditMeasureState.Ready).measure.uid,
                        date = (state as EditMeasureState.Ready).measure.date,
                    )
                )
                assertEquals(
                    initialState, state
                )

                updateMeasureMethod(MeasureMethod.FROM_SCALE)
                assertEquals(
                    initialState.copy(
                        measure = initialState.measure.copy(
                            uid = (state as EditMeasureState.Ready).measure.uid,
                            date = (state as EditMeasureState.Ready).measure.date,
                            measureMethod = MeasureMethod.FROM_SCALE
                        )
                    ), awaitItem()
                )
            }
        }
    }
}
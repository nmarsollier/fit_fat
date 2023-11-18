package com.nmarsollier.fitfat.measures

import app.cash.turbine.test
import com.nmarsollier.fitfat.measures.model.Measure
import com.nmarsollier.fitfat.measures.model.db.MeasureMethod
import com.nmarsollier.fitfat.measures.model.db.MeasureValue
import com.nmarsollier.fitfat.measures.ui.edit.EditMeasureState
import com.nmarsollier.fitfat.measures.ui.edit.EditMeasureViewModel
import com.nmarsollier.fitfat.measures.samples.Samples
import com.nmarsollier.fitfat.userSettings.model.UserSettings
import com.nmarsollier.fitfat.userSettings.samples.Samples
import io.mockk.coVerify
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.koin.test.get
import kotlin.test.assertEquals
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
                assertEquals(
                    EditMeasureState.Loading(
                        null,
                        false
                    ), awaitItem()
                )

                init(null)
                val state = awaitItem()

                assertTrue(state is EditMeasureState.Ready)
                assertEquals(UserSettings.Samples.simpleData.value, state.userSettings)
                assertNotNull(state.measure)
                assertFalse(state.readOnly)

                coVerify { userSettingsRepository.findCurrent() }
                verify { measureDaoMock.findLast() }
            }
        }
    }

    @Test
    fun loadExistingTest(): Unit = runBlocking {
        EditMeasureViewModel(get(), get()).apply {
            state.test {
                assertEquals(
                    EditMeasureState.Loading(
                        null,
                        false
                    ), awaitItem()
                )

                init(Measure.Samples.bodyFat.value)
                assertEquals(
                    EditMeasureState.Loading(
                        Measure.Samples.bodyFat.value, true
                    ), awaitItem()
                )

                assertEquals(
                    EditMeasureState.Ready(
                        UserSettings.Samples.simpleData.value,
                        Measure.Samples.simpleData.first().value,
                        null,
                        false,
                        true
                    ), awaitItem()
                )

                coVerify { userSettingsRepository.findCurrent() }
                verify { measureDaoMock.findLast() }
            }
        }
    }

    @Test
    fun closeTest(): Unit = runBlocking {
        EditMeasureViewModel(get(), get()).apply {
            state.test {
                assertEquals(
                    EditMeasureState.Loading(
                        null,
                        false
                    ), awaitItem()
                )

                close()
                assertEquals(
                    EditMeasureState.Close,
                    awaitItem()
                )
            }
        }
    }

    @Test
    fun updateMeasureValueTest(): Unit = runBlocking {
        var initialState = EditMeasureState.Ready(
            UserSettings.Samples.simpleData.value,
            Measure.Samples.simpleData.last().value,
            null,
            false,
            false
        )

        EditMeasureViewModel(get(), get()).apply {
            state.test {
                assertEquals(
                    EditMeasureState.Loading(
                        null,
                        false
                    ), awaitItem()
                )
                init(null)

                val state = awaitItem()
                initialState = initialState.copy(
                    measure = initialState.measure.copy(
                        uid = (state as EditMeasureState.Ready).measure.uid,
                        date = (state).measure.date,
                    )
                )
                assertEquals(
                    initialState, state
                )

                updateMeasureValue(
                    MeasureValue.BODY_WEIGHT,
                    180
                )
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
            UserSettings.Samples.simpleData.value,
            Measure.Samples.simpleData.last().value,
            null,
            false,
            false
        )

        EditMeasureViewModel(get(), get()).apply {
            state.test {
                assertEquals(
                    EditMeasureState.Loading(
                        null,
                        false
                    ), awaitItem()
                )

                init(null)
                val state = awaitItem()
                initialState = initialState.copy(
                    measure = initialState.measure.copy(
                        uid = (state as EditMeasureState.Ready).measure.uid,
                        date = state.measure.date,
                    )
                )
                assertEquals(
                    initialState, state
                )

                updateMeasureMethod(MeasureMethod.FROM_SCALE)
                assertEquals(
                    initialState.copy(
                        measure = initialState.measure.copy(
                            uid = state.measure.uid,
                            date = state.measure.date,
                            measureMethod = MeasureMethod.FROM_SCALE
                        )
                    ), awaitItem()
                )
            }
        }
    }
}
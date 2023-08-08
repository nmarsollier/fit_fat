package com.nmarsollier.fitfat.viewModels

import app.cash.turbine.*
import com.nmarsollier.fitfat.models.measures.*
import com.nmarsollier.fitfat.models.measures.db.*
import com.nmarsollier.fitfat.models.userSettings.*
import com.nmarsollier.fitfat.testUtils.*
import com.nmarsollier.fitfat.ui.measures.*
import com.nmarsollier.fitfat.ui.measures.edit.*
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
class EditMeasureViewModelTest : BaseTest() {
    @Test
    fun loadNewTest(): Unit = runBlocking {
        EditMeasureViewModel(get(), get(), get()).apply {
            state.test {
                assertEquals(
                    EditMeasureState.Loading(
                        null, false
                    ), awaitItem()
                )

                reduce(EditMeasureAction.Initialize(null))

                val state = awaitItem()

                assertTrue(state is EditMeasureState.Ready)
                assertEquals(UserSettings.Samples.simpleData, state.userSettings)
                assertNotNull(state.measure)
                assertFalse(state.readOnly)

                coVerify { userDaoMock.findCurrent() }
                coVerify { measureDaoMock.findLast() }
            }
        }
    }

    @Test
    fun loadExistingTest(): Unit = runBlocking {
        EditMeasureViewModel(get(), get(), get()).apply {
            state.test {
                assertEquals(
                    EditMeasureState.Loading(
                        measure = null, readOnly = false
                    ), awaitItem()
                )

                reduce(EditMeasureAction.Initialize(Measure.Samples.bodyFat))

                assertEquals(
                    EditMeasureState.Loading(
                        Measure.Samples.bodyFat, true
                    ), awaitItem()
                )

                assertEquals(
                    EditMeasureState.Ready(
                        isSaveEnabled = true,
                        userSettings = UserSettings.Samples.simpleData,
                        measure = Measure.Samples.simpleData.first(),
                        showHelp = null,
                        showMeasureMethod = false,
                        readOnly = true
                    ), awaitItem()
                )

                coVerify { userDaoMock.findCurrent() }
                coVerify { measureDaoMock.findLast() }
            }
        }
    }

    @Test
    fun closeTest(): Unit = runTest {
        EditMeasureViewModel(get(), get(), get()).apply {
            state.test {
                assertEquals(
                    EditMeasureState.Loading(
                        measure = null, readOnly = false
                    ), awaitItem()
                )
            }
            event.test {
                reduce(EditMeasureAction.Close)

                assertEquals(
                    EditMeasureEvent.Close, awaitItem()
                )
            }
        }
    }

    @Test
    fun updateMeasureValueTest(): Unit = runBlocking {
        var initialState = EditMeasureState.Ready(
            isSaveEnabled = true,
            userSettings = UserSettings.Samples.simpleData,
            measure = Measure.Samples.simpleData.last(),
            showHelp = null,
            showMeasureMethod = false,
            readOnly = false
        )

        EditMeasureViewModel(get(), get(), get()).apply {
            state.test {
                assertEquals(
                    EditMeasureState.Loading(
                        null, false
                    ), awaitItem()
                )

                reduce(EditMeasureAction.Initialize(null))

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

                reduce(EditMeasureAction.UpdateMeasureValue(MeasureValue.BODY_WEIGHT, 180))

                assertEquals(
                    initialState.copy(
                        measure = initialState.measure.copy(
                            bodyWeight = 180.0, fatPercent = 0.0
                        )
                    ), awaitItem()
                )
            }
        }
    }

    @Test
    fun updateMeasureMethodTest(): Unit = runBlocking {
        var initialState = EditMeasureState.Ready(
            isSaveEnabled = true,
            userSettings = UserSettings.Samples.simpleData,
            measure = Measure.Samples.simpleData.last(),
            showHelp = null,
            showMeasureMethod = false,
            readOnly = false
        )

        EditMeasureViewModel(get(), get(), get()).apply {
            state.test {
                assertEquals(
                    EditMeasureState.Loading(
                        null, false
                    ), awaitItem()
                )

                reduce(EditMeasureAction.Initialize(null))
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

                reduce(EditMeasureAction.UpdateMeasureMethod(MeasureMethod.FROM_SCALE))
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
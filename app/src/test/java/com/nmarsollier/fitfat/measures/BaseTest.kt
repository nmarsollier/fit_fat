package com.nmarsollier.fitfat.measures

import com.nmarsollier.fitfat.firebase.FirebaseConnection
import com.nmarsollier.fitfat.measures.model.Measure
import com.nmarsollier.fitfat.measures.model.UploadMeasuresFirebaseService
import com.nmarsollier.fitfat.measures.model.db.MeasureDao
import com.nmarsollier.fitfat.measures.model.db.MeasureDao_Impl
import com.nmarsollier.fitfat.measures.model.MeasuresRepository
import com.nmarsollier.fitfat.measures.model.api.MeasuresFirebaseApi
import com.nmarsollier.fitfat.measures.ui.editMeasure.EditMeasureViewModel
import com.nmarsollier.fitfat.measures.ui.measuresList.MeasuresListViewModel
import com.nmarsollier.fitfat.measures.ui.utils.preview.Samples
import com.nmarsollier.fitfat.userSettings.model.UserSettings
import com.nmarsollier.fitfat.userSettings.model.UserSettingsRepository
import com.nmarsollier.fitfat.userSettings.ui.utils.preview.Samples
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.setMain
import org.junit.Rule
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule

@ExperimentalCoroutinesApi
open class BaseTest : KoinTest {

    val userSettingsRepository =
        mockk<UserSettingsRepository>(relaxed = true) {
            coEvery { findCurrent() } answers {
                UserSettings.Samples.simpleData
            }
        }

    val measureDaoMock =
        mockk<MeasureDao_Impl>(relaxed = true) {
            every { findAll() } returns (Measure.Samples.simpleData.map { it.value })
            every { findLast() } returns (Measure.Samples.simpleData.last().value)
            every { findUnSynced() } returns (Measure.Samples.simpleData.map { it.value })
            every { findById(any()) } returns (Measure.Samples.simpleData.first().value)
        }

    val firebaseRepositoryMock = mockk<MeasuresFirebaseApi>(relaxed = true) {
        coEvery { findAll(any()) } returns (Measure.Samples.simpleData)
        coEvery { update(any()) } returns (
                mockk(relaxed = true) {
                    every { isSuccessful } returns (true)
                })
    }

    val firebaseConnectionMock = mockk<FirebaseConnection>(relaxed = true) {

    }

    val measuresTestModule = module {
        factory<MeasureDao> { measureDaoMock }
        factory { userSettingsRepository }

        factoryOf(::MeasuresRepository)
        factory { firebaseRepositoryMock }
        factoryOf(::UploadMeasuresFirebaseService)

        factory { firebaseConnectionMock }

        viewModelOf(::MeasuresListViewModel)
        viewModelOf(::EditMeasureViewModel)
    }

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        // Your KoinApplication instance here
        modules(
            measuresTestModule
        )
    }

    init {
        Dispatchers.setMain(Dispatchers.Default)
        Dispatchers.setMain(Dispatchers.Unconfined)
    }
}
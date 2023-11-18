package com.nmarsollier.fitfat.userSettings

import com.nmarsollier.fitfat.common.firebase.FirebaseConnection
import com.nmarsollier.fitfat.userSettings.model.UploadSyncFirebaseService
import com.nmarsollier.fitfat.userSettings.model.db.UserSettingsDao_Impl
import com.nmarsollier.fitfat.userSettings.model.UserSettingsRepository
import com.nmarsollier.fitfat.userSettings.ui.OptionsView
import com.nmarsollier.fitfat.userSettings.samples.Samples
import io.mockk.coEvery
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

    val userDaoMock = mockk<UserSettingsDao_Impl>(relaxed = true) {
        coEvery { findCurrent() } answers {
            com.nmarsollier.fitfat.userSettings.model.UserSettings.Samples.simpleData.value
        }
    }

    val firebaseRepositoryMock = mockk<com.nmarsollier.fitfat.userSettings.model.api.UserSettingsFirebaseApi>(relaxed = true) {
        coEvery { findCurrent() } returns (com.nmarsollier.fitfat.userSettings.model.api.FirebaseUserSettingsData(
            displayName = "Test",
            birthDate = com.nmarsollier.fitfat.common.converters.dateOf(2021, 10, 10),
            weight = 80.0,
            height = 180.0,
            sex = com.nmarsollier.fitfat.userSettings.model.db.UserSettingsData.SexType.FEMALE,
            measureSystem = com.nmarsollier.fitfat.userSettings.model.db.UserSettingsData.MeasureType.METRIC
        ))
    }

    val firebaseConnectionMock = mockk<FirebaseConnection>(relaxed = true) {

    }

    val measuresTestModule = module {
        factory<com.nmarsollier.fitfat.userSettings.model.db.UserSettingsDao> { userDaoMock }

        factory { firebaseRepositoryMock }

        factory { firebaseConnectionMock }

        factoryOf(::UploadSyncFirebaseService)
        factoryOf(::UserSettingsRepository)
        viewModelOf(::OptionsView)
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
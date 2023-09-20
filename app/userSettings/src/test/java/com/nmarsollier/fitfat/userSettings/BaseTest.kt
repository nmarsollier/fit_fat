package com.nmarsollier.fitfat.userSettings

import com.nmarsollier.fitfat.firebase.FirebaseConnection
import com.nmarsollier.fitfat.userSettings.model.UploadSyncFirebaseService
import com.nmarsollier.fitfat.userSettings.model.UserSettings
import com.nmarsollier.fitfat.userSettings.model.db.UserSettingsDao
import com.nmarsollier.fitfat.userSettings.model.db.UserSettingsDao_Impl
import com.nmarsollier.fitfat.userSettings.model.db.UserSettingsData
import com.nmarsollier.fitfat.userSettings.model.api.FirebaseUserSettingsData
import com.nmarsollier.fitfat.userSettings.model.UserSettingsRepository
import com.nmarsollier.fitfat.userSettings.model.api.UserSettingsFirebaseApi
import com.nmarsollier.fitfat.userSettings.ui.options.OptionsViewModel
import com.nmarsollier.fitfat.userSettings.ui.utils.preview.Samples
import com.nmarsollier.fitfat.utils.converters.dateOf
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
            UserSettings.Samples.simpleData.value
        }
    }

    val firebaseRepositoryMock = mockk<UserSettingsFirebaseApi>(relaxed = true) {
        coEvery { findCurrent() } returns (FirebaseUserSettingsData(
            displayName = "Test",
            birthDate = dateOf(2021, 10, 10),
            weight = 80.0,
            height = 180.0,
            sex = UserSettingsData.SexType.FEMALE,
            measureSystem = UserSettingsData.MeasureType.METRIC
        ))
    }

    val firebaseConnectionMock = mockk<FirebaseConnection>(relaxed = true) {

    }

    val measuresTestModule = module {
        factory<UserSettingsDao> { userDaoMock }

        factory { firebaseRepositoryMock }

        factory { firebaseConnectionMock }

        factoryOf(::UploadSyncFirebaseService)
        factoryOf(::UserSettingsRepository)
        viewModelOf(::OptionsViewModel)
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
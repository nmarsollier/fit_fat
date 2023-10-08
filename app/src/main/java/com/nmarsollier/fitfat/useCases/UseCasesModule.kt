package com.nmarsollier.fitfat.useCases

import com.nmarsollier.fitfat.model.firebase.FirebaseDao
import com.nmarsollier.fitfat.model.firebase.FirebaseRepository
import com.nmarsollier.fitfat.model.measures.MeasuresRepository
import com.nmarsollier.fitfat.model.userSettings.UserSettingsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class, FragmentComponent::class, ActivityComponent::class)
object UseCasesModule {

    @Provides
    fun provideFirebaseUseCases(
        userSettingsRepository: UserSettingsRepository,
        measuresRepository: MeasuresRepository,
        firebaseRepository: FirebaseRepository,
        googleUseCase: GoogleUseCase
    ) = FirebaseUseCase(
        userSettingsRepository, measuresRepository, firebaseRepository, googleUseCase
    )

    @Provides
    fun provideGoogleUseCase() = GoogleUseCase()

    @Provides
    fun provideUploadPendingMeasuresUseCase(
        firebaseDao: FirebaseDao,
        firebaseUseCase: FirebaseUseCase,
        measuresRepository: MeasuresRepository
    ) = UpdatePendingMeasuresUseCase(firebaseDao, firebaseUseCase, measuresRepository)
}

package com.nmarsollier.fitfat.model

import android.content.Context
import com.nmarsollier.fitfat.model.db.FitFatDatabase
import com.nmarsollier.fitfat.model.db.getRoomDatabase
import com.nmarsollier.fitfat.model.firebase.FirebaseDao
import com.nmarsollier.fitfat.model.firebase.FirebaseRepository
import com.nmarsollier.fitfat.model.measures.MeasuresRepository
import com.nmarsollier.fitfat.model.userSettings.UserSettingsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): FitFatDatabase = getRoomDatabase(context)

    @Provides
    @Singleton
    fun provideMeasureRepository(
        database: FitFatDatabase, firebaseRepository: FirebaseRepository
    ) = MeasuresRepository(database, firebaseRepository)

    @Provides
    @Singleton
    fun provideUserSettingsRepository(
        database: FitFatDatabase
    ) = UserSettingsRepository(database)

    @Provides
    @Singleton
    fun provideFirebaseDao() = FirebaseDao()

    @Provides
    @Singleton
    fun provideFirebaseRepository(
        firebaseDao: FirebaseDao, userSettingsRepository: UserSettingsRepository
    ) = FirebaseRepository(firebaseDao, userSettingsRepository)
}
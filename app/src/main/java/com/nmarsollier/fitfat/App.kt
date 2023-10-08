package com.nmarsollier.fitfat

import android.app.Application
import com.nmarsollier.fitfat.model.koinDatabaseModule
import com.nmarsollier.fitfat.ui.koinUiModule
import com.nmarsollier.fitfat.useCases.koinUseCaseModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()

            androidContext(this@App)

            modules(koinDatabaseModule, koinUseCaseModules, koinUiModule)
        }
    }
}

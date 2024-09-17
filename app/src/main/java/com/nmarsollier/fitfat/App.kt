package com.nmarsollier.fitfat

import android.app.Application
import com.nmarsollier.fitfat.models.modelsModule
import com.nmarsollier.fitfat.ui.uiModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(
                modelsModule,
                uiModule,
            )
        }
    }
}

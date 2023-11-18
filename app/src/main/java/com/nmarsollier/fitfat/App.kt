package com.nmarsollier.fitfat

import android.app.Application
import com.nmarsollier.fitfat.common.firebase.koinFirebaseModule
import com.nmarsollier.fitfat.stats.koinStatsModule
import com.nmarsollier.fitfat.userSettings.userSettingsModule
import com.nmarsollier.fitfat.common.koinUtilsModule
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
                com.nmarsollier.fitfat.measures.measuresModule,
                dashboardModule,
                koinUtilsModule,
                koinFirebaseModule,
                userSettingsModule,
                koinStatsModule
            )
        }
    }
}

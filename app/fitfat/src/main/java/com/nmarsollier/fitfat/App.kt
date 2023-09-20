package com.nmarsollier.fitfat

import android.app.Application
import com.nmarsollier.fitfat.firebase.koinFirebaseModule
import com.nmarsollier.fitfat.measures.measuresModule
import com.nmarsollier.fitfat.stats.koinStatsModule
import com.nmarsollier.fitfat.userSettings.userSettingsModule
import com.nmarsollier.fitfat.utils.koinUtilsModule
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
                measuresModule,
                dashboardModule,
                koinUtilsModule,
                koinFirebaseModule,
                userSettingsModule,
                koinStatsModule
            )
        }
    }
}

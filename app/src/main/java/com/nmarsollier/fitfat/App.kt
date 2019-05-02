package com.nmarsollier.fitfat

import android.app.Application
import com.evernote.android.state.StateSaver
import com.nmarsollier.fitfat.model.FirebaseDao

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        StateSaver.setEnabledForAllActivitiesAndSupportFragments(this, true)
        FirebaseDao.init(this)
    }
}

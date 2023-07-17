package com.nmarsollier.fitfat

import android.app.Application
import com.nmarsollier.fitfat.model.FirebaseDao

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseDao.init(this)
    }
}

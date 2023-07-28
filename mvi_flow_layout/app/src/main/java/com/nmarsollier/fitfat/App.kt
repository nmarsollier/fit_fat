package com.nmarsollier.fitfat

import android.app.Application
import android.content.Context
import com.nmarsollier.fitfat.model.firebase.FirebaseRepository
import com.nmarsollier.fitfat.utils.collectOnce
import com.nmarsollier.fitfat.utils.logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class App : Application() {
    override fun onCreate() {
        appContext = applicationContext
        MainScope().launch(Dispatchers.IO) {
            super.onCreate()
            FirebaseRepository.init().collectOnce {
                logger.info("Firebase service started : $it")
            }
        }
    }

    companion object {
        lateinit var appContext: Context
    }
}

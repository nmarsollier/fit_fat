package com.nmarsollier.fitfat

import android.app.Application
import com.nmarsollier.fitfat.model.firebase.FirebaseRepository
import com.nmarsollier.fitfat.utils.logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class App : Application() {
    private val logger by logger()

    override fun onCreate() {
        MainScope().launch(Dispatchers.IO) {
            super.onCreate()
            FirebaseRepository.init(applicationContext).collect {
                logger.info("Firebase service started : $it")
            }
        }
    }
}

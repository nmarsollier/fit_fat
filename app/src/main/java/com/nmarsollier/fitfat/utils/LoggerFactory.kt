package com.nmarsollier.fitfat.utils

import java.util.logging.Logger

inline fun logger() = object : Lazy<Logger> {
    private var logger: Logger? = null

    override val value: Logger
        get() = logger ?: Logger.getLogger("FitFat").also {
            logger = it
        }

    override fun isInitialized() = logger != null
}
package com.nmarsollier.fitfat.common.logger

import java.util.logging.Level
import java.util.logging.Logger

class Logger internal constructor() {
    val logger: Logger = Logger.getLogger("FitFat")
    fun e(message: String?, e: Throwable?) = logger.log(Level.SEVERE, message, e)
    fun i(message: String?) = logger.log(Level.INFO, message)
}
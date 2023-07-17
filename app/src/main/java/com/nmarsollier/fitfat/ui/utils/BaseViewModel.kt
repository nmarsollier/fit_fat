package com.nmarsollier.fitfat.ui.utils

import androidx.lifecycle.ViewModel
import com.nmarsollier.fitfat.utils.LiveEvent

abstract class BaseViewModel<T : Any>(initial: T) : ViewModel() {
    val state: LiveEvent<T> by lazy {
        LiveEvent<T>().also { it.emit(initial) }
    }
}
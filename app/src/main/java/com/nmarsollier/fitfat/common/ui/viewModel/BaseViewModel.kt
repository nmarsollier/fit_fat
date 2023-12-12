package com.nmarsollier.fitfat.common.ui.viewModel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

abstract class BaseViewModel<S : Any, E : Any>(initial: S) : ViewModel() {
    private val mutableState: MutableStateFlow<S> by lazy {
        MutableStateFlow(initial)
    }

    val state: StateFlow<S> by lazy {
        mutableState.asStateFlow()
    }

    fun S.sendToState() {
        val value = this
        mutableState.update {
            value
        }
    }

    abstract fun reduce(event: E)
}
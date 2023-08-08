package com.nmarsollier.fitfat.models.common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

sealed interface RepositoryUpdate {
    data object Save : RepositoryUpdate
    data object Delete : RepositoryUpdate
}

abstract class StateRepository {
    private val mutableUpdateFlow = MutableSharedFlow<RepositoryUpdate?>(replay = 0)
    val updateFlow: SharedFlow<RepositoryUpdate?> = mutableUpdateFlow

    fun RepositoryUpdate.sendToEvent() {
        val thisEvent = this
        CoroutineScope(Dispatchers.IO).launch {
            mutableUpdateFlow.emit(thisEvent)
        }
    }
}
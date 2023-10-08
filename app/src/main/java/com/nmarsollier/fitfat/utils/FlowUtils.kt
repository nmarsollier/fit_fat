package com.nmarsollier.fitfat.utils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.take

suspend fun <T> Flow<out T>.collectOnce(collector: FlowCollector<T>) {
    this.take(1).collect(collector)
}

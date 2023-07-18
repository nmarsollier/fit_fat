package com.nmarsollier.fitfat.ui.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

fun <T> StateFlow<T>.observe(scope: CoroutineScope, collector: FlowCollector<T>) = scope.launch {
    collect(collector)
}

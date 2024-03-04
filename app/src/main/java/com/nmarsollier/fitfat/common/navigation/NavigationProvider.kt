package com.nmarsollier.fitfat.common.navigation

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

class NavigationProvider {
    private val statusUpdateChannel = Channel<Long>()

    private var appNavActionsReference = WeakReference<AppNavActions>(null)

    var appNavActions: AppNavActions?
        get() = appNavActionsReference.get()
        set(value) {
            appNavActionsReference = WeakReference(value)

            MainScope().launch(Dispatchers.Main) {
                statusUpdateChannel.send(System.currentTimeMillis())
            }
        }
}


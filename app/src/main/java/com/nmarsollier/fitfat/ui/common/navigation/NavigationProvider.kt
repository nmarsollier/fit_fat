package com.nmarsollier.fitfat.ui.common.navigation

import java.lang.ref.WeakReference

class NavigationProvider {
    private var appNavActionsReference = WeakReference<AppNavActions>(null)

    var appNavActions: AppNavActions?
        get() = appNavActionsReference.get()
        set(value) {
            appNavActionsReference = WeakReference(value)
        }
}

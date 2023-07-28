package com.nmarsollier.fitfat.ui.main

import androidx.lifecycle.viewModelScope
import com.nmarsollier.fitfat.model.userSettings.UserSettings
import com.nmarsollier.fitfat.model.userSettings.UserSettingsRepository
import com.nmarsollier.fitfat.utils.BaseViewModel
import com.nmarsollier.fitfat.utils.collectOnce
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class Screen {
    OPTIONS, HOME, PROGRESS
}

sealed class MainState(
    val selectedTab: Screen
) {
    data class Loading(private val tab: Screen) : MainState(tab)

    data class Ready(
        private val tab: Screen, val userSettings: UserSettings
    ) : MainState(tab)
}

class MainViewModel : BaseViewModel<MainState>(MainState.Loading(Screen.HOME)) {
    init {
        viewModelScope.launch(Dispatchers.IO) {
            UserSettingsRepository.load().collectOnce { data ->
                mutableState.update {
                    MainState.Ready(
                        tab = if (data.isNew()) {
                            Screen.OPTIONS
                        } else {
                            Screen.HOME
                        }, userSettings = data
                    )
                }
            }
        }
    }

    fun setCurrentSelectedTab(screen: Screen) {
        mutableState.update {
            when (val value = state.value) {
                is MainState.Loading -> value.copy(tab = screen)
                is MainState.Ready -> value.copy(tab = screen)
            }
        }
    }
}
package com.nmarsollier.fitfat.ui.main

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.nmarsollier.fitfat.model.userSettings.UserSettings
import com.nmarsollier.fitfat.model.userSettings.UserSettingsRepository
import com.nmarsollier.fitfat.ui.utils.BaseViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class Screen {
    OPTIONS, HOME, PROGRESS
}

sealed class MainState(
    val selectedTab: Screen
) {
    data class Initial(private val tab: Screen) : MainState(tab)
    data class Loading(private val tab: Screen) : MainState(tab)

    data class Ready(
        private val tab: Screen,
        val userSettings: UserSettings
    ) : MainState(tab)
}

class MainViewModel : BaseViewModel<MainState>(MainState.Initial(Screen.HOME)) {
    private val currentTab
        get() = state.value?.selectedTab ?: Screen.HOME

    fun load(context: Context) = viewModelScope.launch {
        mutableState.update { MainState.Loading(currentTab) }

        UserSettingsRepository.load(context).collect { data ->
            mutableState.update {
                MainState.Ready(
                    tab = if (data.isNew()) {
                        Screen.OPTIONS
                    } else {
                        Screen.HOME
                    },
                    userSettings = data
                )
            }
        }
    }

    fun setCurrentSelectedTab(screen: Screen) {
        mutableState.update {
            when (val value = state.value) {
                is MainState.Initial -> value.copy(tab = screen)
                is MainState.Loading -> value.copy(tab = screen)
                is MainState.Ready -> value.copy(tab = screen)
                null -> MainState.Initial(Screen.HOME)
            }
        }
    }
}
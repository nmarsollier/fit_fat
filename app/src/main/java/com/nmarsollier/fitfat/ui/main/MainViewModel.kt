package com.nmarsollier.fitfat.ui.main

import androidx.lifecycle.viewModelScope
import com.nmarsollier.fitfat.model.userSettings.UserSettings
import com.nmarsollier.fitfat.model.userSettings.UserSettingsRepository
import com.nmarsollier.fitfat.utils.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

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

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userSettingsRepository: UserSettingsRepository
) : BaseViewModel<MainState>(MainState.Loading(Screen.HOME)) {
    init {
        viewModelScope.launch(Dispatchers.IO) {
            userSettingsRepository.load().let { data ->
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
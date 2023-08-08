package com.nmarsollier.fitfat.ui.dashboard

import androidx.lifecycle.viewModelScope
import com.nmarsollier.fitfat.model.userSettings.UserSettingsRepository
import com.nmarsollier.fitfat.utils.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class Screen {
    OPTIONS, MEASURES_LIST, STATS
}

sealed class DashboardState(
    val selectedTab: Screen
) {
    data class Loading(private val tab: Screen) : DashboardState(tab)

    data class Ready(
        private val tab: Screen
    ) : DashboardState(tab)
}

interface DashboardReducer {
    fun setCurrentSelectedTab(screen: Screen)
}

class DashboardViewModel(
    private val userSettingsRepository: UserSettingsRepository
) : BaseViewModel<DashboardState>(DashboardState.Loading(Screen.MEASURES_LIST)), DashboardReducer {
    fun init() {
        mutableState.update { DashboardState.Loading(Screen.MEASURES_LIST) }
        viewModelScope.launch(Dispatchers.IO) {
            val isNew = userSettingsRepository.load().isNew()
            mutableState.update {
                DashboardState.Ready(
                    tab = if (isNew) {
                        Screen.OPTIONS
                    } else {
                        Screen.MEASURES_LIST
                    }
                )
            }
        }
    }

    override fun setCurrentSelectedTab(screen: Screen) {
        mutableState.update {
            when (val value = state.value) {
                is DashboardState.Loading -> value.copy(tab = screen)
                is DashboardState.Ready -> value.copy(tab = screen)
            }
        }
    }

    companion object
}
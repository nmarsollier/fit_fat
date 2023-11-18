package com.nmarsollier.fitfat.ui.dashboard

import androidx.lifecycle.viewModelScope
import com.nmarsollier.fitfat.userSettings.model.UserSettingsRepository
import com.nmarsollier.fitfat.utils.ui.viewModel.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

enum class Screen {
    OPTIONS, MEASURES_LIST, STATS
}

sealed class DashboardState {
    data class Loading(internal val tab: Screen) : DashboardState()

    data class Ready(internal val tab: Screen) : DashboardState()

    val selectedTab
        get() = when (this) {
            is Loading -> this.tab
            is Ready -> this.tab
        }

    fun updateTab(tab: Screen) = when (this) {
        is Loading -> this.copy(tab = tab)
        is Ready -> this.copy(tab = tab)
    }
}

interface DashboardReducer {
    fun setCurrentSelectedTab(screen: Screen)
}

class DashboardViewModel(
    private val userSettingsRepository: UserSettingsRepository
) : BaseViewModel<DashboardState>(DashboardState.Loading(Screen.MEASURES_LIST)), DashboardReducer {
    fun init() {
        DashboardState.Loading(Screen.MEASURES_LIST).sendToState()
        viewModelScope.launch(Dispatchers.IO) {
            val isNew = userSettingsRepository.findCurrent().value.isNew()
            DashboardState.Ready(
                tab = if (isNew) {
                    Screen.OPTIONS
                } else {
                    Screen.MEASURES_LIST
                }
            ).sendToState()
        }
    }

    override fun setCurrentSelectedTab(screen: Screen) {
        state.value.updateTab(screen).sendToState()
    }

    companion object
}
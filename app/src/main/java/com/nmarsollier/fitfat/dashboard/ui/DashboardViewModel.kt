package com.nmarsollier.fitfat.dashboard.ui

import androidx.lifecycle.viewModelScope
import com.nmarsollier.fitfat.userSettings.model.UserSettingsRepository
import com.nmarsollier.fitfat.common.ui.viewModel.BaseViewModel
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

sealed class DashboardEvent {
    data object Initialize : DashboardEvent()
    data class CurrentSelectedTab(val screen: Screen) : DashboardEvent()
}

class DashboardViewModel(
    private val userSettingsRepository: UserSettingsRepository
) : BaseViewModel<DashboardState, DashboardEvent>(DashboardState.Loading(Screen.MEASURES_LIST)) {

    override fun reduce(event: DashboardEvent) = when (event) {
        DashboardEvent.Initialize -> init()
        is DashboardEvent.CurrentSelectedTab -> setCurrentSelectedTab(event)
    }

    private fun init() {
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

    private fun setCurrentSelectedTab(event: DashboardEvent.CurrentSelectedTab) {
        state.value.updateTab(event.screen).sendToState()
    }

    companion object
}
package com.nmarsollier.fitfat.ui.dashboard

import androidx.lifecycle.viewModelScope
import com.nmarsollier.fitfat.common.uiUtils.StateViewModel
import com.nmarsollier.fitfat.models.userSettings.UserSettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

enum class Screen {
    OPTIONS, MEASURES_LIST, STATS
}

sealed interface DashboardState {
    data class Loading(internal val tab: Screen) : DashboardState

    data class Ready(internal val tab: Screen) : DashboardState

    val selectedTab
        get() = when (this) {
            is Loading -> this.tab
            is Ready -> this.tab
        }
}

sealed interface DashboardEvent

sealed interface DashboardAction {
    data object Initialize : DashboardAction
    data class CurrentSelectedTab(val screen: Screen) : DashboardAction
}

class DashboardViewModel(
    private val userSettingsRepository: UserSettingsRepository
) : StateViewModel<DashboardState, DashboardEvent, DashboardAction>(DashboardState.Loading(Screen.MEASURES_LIST)) {
    init {
        println("DashboardViewModel created")
    }

    override fun reduce(action: DashboardAction) = when (action) {
        DashboardAction.Initialize -> init()
        is DashboardAction.CurrentSelectedTab -> setCurrentSelectedTab(action)
    }

    private fun init() {
        DashboardState.Loading(Screen.MEASURES_LIST).sendToState()
        viewModelScope.launch(Dispatchers.IO) {
            val isNew = userSettingsRepository.findCurrent().isNew()
            DashboardState.Ready(
                tab = if (isNew) {
                    Screen.OPTIONS
                } else {
                    Screen.MEASURES_LIST
                }
            ).sendToState()
        }
    }

    private fun setCurrentSelectedTab(event: DashboardAction.CurrentSelectedTab) {
        if (state.value.selectedTab == event.screen) return
        
        when (val st = state.value) {
            is DashboardState.Loading -> st.copy(tab = event.screen)
            is DashboardState.Ready -> st.copy(tab = event.screen)
        }.sendToState()
    }

    companion object
}
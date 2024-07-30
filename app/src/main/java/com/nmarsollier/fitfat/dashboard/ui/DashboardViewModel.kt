package com.nmarsollier.fitfat.dashboard.ui

import androidx.lifecycle.viewModelScope
import com.nmarsollier.fitfat.common.ui.viewModel.StateViewModel
import com.nmarsollier.fitfat.dashboard.ui.DashboardState.Loading
import com.nmarsollier.fitfat.dashboard.ui.DashboardState.Ready
import com.nmarsollier.fitfat.userSettings.model.UserSettingsRepository
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
) : StateViewModel<DashboardState, DashboardEvent, DashboardAction>(Loading(Screen.MEASURES_LIST)) {

    override fun reduce(action: DashboardAction) = when (action) {
        DashboardAction.Initialize -> init()
        is DashboardAction.CurrentSelectedTab -> setCurrentSelectedTab(action)
    }

    private fun init() {
        Loading(Screen.MEASURES_LIST).sendToState()
        viewModelScope.launch(Dispatchers.IO) {
            val isNew = userSettingsRepository.findCurrent().value.isNew()
            Ready(
                tab = if (isNew) {
                    Screen.OPTIONS
                } else {
                    Screen.MEASURES_LIST
                }
            ).sendToState()
        }
    }

    private fun setCurrentSelectedTab(event: DashboardAction.CurrentSelectedTab) {
        when (val st = state.value) {
            is Loading -> st.copy(tab = event.screen)
            is Ready -> st.copy(tab = event.screen)
        }.sendToState()
    }

    companion object
}
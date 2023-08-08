package com.nmarsollier.fitfat.ui.dashboard

import androidx.compose.runtime.*
import androidx.lifecycle.*
import com.nmarsollier.fitfat.models.userSettings.*
import com.nmarsollier.fitfat.ui.common.viewModel.*
import kotlinx.coroutines.*

enum class Screen {
    OPTIONS, MEASURES_LIST, STATS
}

@Stable
@Immutable
data class DashboardState(internal val tab: Screen)

sealed interface DashboardEvent

sealed interface DashboardAction {
    data class CurrentSelectedTab(val screen: Screen) : DashboardAction
}

class DashboardViewModel(
    private val userSettingsRepository: UserSettingsRepository
) : StateViewModel<DashboardState, DashboardEvent, DashboardAction>(
    DashboardState(
        Screen.MEASURES_LIST
    )
) {
    init {
        init()
    }

    override fun reduce(action: DashboardAction) {
        when (action) {
            is DashboardAction.CurrentSelectedTab -> setCurrentSelectedTab(action)
        }
    }

    fun init() {
        viewModelScope.launch(Dispatchers.IO) {
            val isNew = userSettingsRepository.findCurrent().isNew()
            if (isNew) {
                DashboardState(Screen.OPTIONS).sendToState()
            }
        }
    }

    fun setCurrentSelectedTab(event: DashboardAction.CurrentSelectedTab) {
        if (event.screen != state.value.tab) {
            DashboardState(event.screen).sendToState()
        }
    }

    companion object
}
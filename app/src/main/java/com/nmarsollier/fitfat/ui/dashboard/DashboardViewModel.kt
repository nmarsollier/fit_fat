package com.nmarsollier.fitfat.ui.dashboard

import androidx.compose.runtime.*
import androidx.lifecycle.*
import com.nmarsollier.fitfat.models.userSettings.*
import com.nmarsollier.fitfat.ui.common.viewModel.*
import kotlinx.coroutines.*

@Stable
enum class Screen {
    OPTIONS, MEASURES_LIST, STATS
}

class DashboardViewModel(
    private val userSettingsRepository: UserSettingsRepository
) : StateViewModel<Screen, Void, Screen>(
    Screen.MEASURES_LIST
) {
    init {
        init()
    }

    override fun reduce(action: Screen) {
        setCurrentSelectedTab(action)
    }

    fun init() {
        viewModelScope.launch(Dispatchers.IO) {
            val isNew = userSettingsRepository.findCurrent().isNew()
            if (isNew) {
                Screen.OPTIONS.sendToState()
            }
        }
    }

    fun setCurrentSelectedTab(event: Screen) {
        if (event != state.value) {
            event.sendToState()
        }
    }

    companion object
}
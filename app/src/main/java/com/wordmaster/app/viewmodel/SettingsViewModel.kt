package com.wordmaster.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.wordmaster.app.WordMasterApp
import com.wordmaster.app.settings.AppSettings
import com.wordmaster.app.settings.SettingsManager
import com.wordmaster.app.settings.ThemeMode
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val manager: SettingsManager = (application as WordMasterApp).settingsManager

    val settings: StateFlow<AppSettings> = manager.settings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppSettings())

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch { manager.setThemeMode(mode) }
    }

    fun setAnswerCount(count: Int) {
        viewModelScope.launch { manager.setAnswerCount(count) }
    }

    fun setTtsSpeed(speed: Float) {
        viewModelScope.launch { manager.setTtsSpeed(speed) }
    }
}

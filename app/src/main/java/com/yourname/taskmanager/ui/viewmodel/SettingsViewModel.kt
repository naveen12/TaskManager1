package com.yourname.taskmanager.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.yourname.taskmanager.utils.SettingsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val settingsManager = SettingsManager(application)

    private val _isDarkTheme = MutableStateFlow(settingsManager.isDarkTheme())
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme

    fun onThemeChange(isDark: Boolean) {
        settingsManager.setDarkTheme(isDark)
        _isDarkTheme.value = isDark
    }
}

package com.yourname.taskmanager.utils

import android.content.Context
import android.content.SharedPreferences

class SettingsManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("TaskManagerPrefs", Context.MODE_PRIVATE)

    fun setFingerprintLockEnabled(isEnabled: Boolean) {
        prefs.edit().putBoolean("fingerprint_lock_enabled", isEnabled).apply()
    }

    fun isFingerprintLockEnabled(): Boolean {
        return prefs.getBoolean("fingerprint_lock_enabled", false)
    }

    fun setDarkTheme(isDark: Boolean) {
        prefs.edit().putBoolean("dark_theme", isDark).apply()
    }

    fun isDarkTheme(): Boolean {
        return prefs.getBoolean("dark_theme", false)
    }
}

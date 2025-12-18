package com.yourname.taskmanager.utils

import android.content.Context
import android.content.SharedPreferences

class SettingsManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)

    fun isFingerprintLockEnabled(): Boolean {
        return prefs.getBoolean("fingerprint_lock", true)
    }

    fun setFingerprintLockEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("fingerprint_lock", enabled).apply()
    }
}

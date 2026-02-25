package com.localagent.data.repository

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepository @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences("local_agent_prefs", Context.MODE_PRIVATE)

    var ollamaBaseUrl: String
        get() = prefs.getString("ollama_base_url", "http://localhost:11434") ?: "http://localhost:11434"
        set(value) {
            prefs.edit().putString("ollama_base_url", value).apply()
        }

    var isMultiAgentMode: Boolean
        get() = prefs.getBoolean("is_multi_agent_mode", false)
        set(value) {
            prefs.edit().putBoolean("is_multi_agent_mode", value).apply()
        }

    // Privacy Settings
    var logSmsCall: Boolean
        get() = prefs.getBoolean("log_sms_call", false)
        set(value) {
            prefs.edit().putBoolean("log_sms_call", value).apply()
        }

    var autoDeleteInterval: Int
        get() = prefs.getInt("auto_delete_interval", 30) // Days
        set(value) {
            prefs.edit().putInt("auto_delete_interval", value).apply()
        }

    var isStealthMode: Boolean
        get() = prefs.getBoolean("is_stealth_mode", false)
        set(value) {
            prefs.edit().putBoolean("is_stealth_mode", value).apply()
        }

    var isFirstLaunch: Boolean
        get() = prefs.getBoolean("is_first_launch", true)
        set(value) {
            prefs.edit().putBoolean("is_first_launch", value).apply()
        }
}

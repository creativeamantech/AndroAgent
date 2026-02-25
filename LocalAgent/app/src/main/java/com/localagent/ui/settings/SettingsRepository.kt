package com.localagent.ui.settings

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
}

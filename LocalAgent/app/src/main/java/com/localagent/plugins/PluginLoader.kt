package com.localagent.plugins

import android.content.Context
import com.localagent.tools.AgentTool
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PluginLoader @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun loadPlugins(): List<AgentTool> {
        // Stub: scan PackageManager for apps declaring a specific meta-data
        // Return a list of tools from those plugins.
        // For now, return empty.
        return emptyList()
    }
}

package com.localagent.plugins

import com.localagent.tools.AgentTool
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PluginRegistry @Inject constructor() {
    private val plugins = mutableListOf<AgentTool>()

    fun loadPlugins() {
        // Stub: Load plugins dynamically
        plugins.add(HomeAssistantPlugin())
    }

    fun getPlugins(): List<AgentTool> = plugins
}

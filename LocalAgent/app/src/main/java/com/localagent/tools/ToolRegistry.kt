package com.localagent.tools

import com.localagent.plugins.PluginRegistry
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ToolRegistry @Inject constructor(
    private val toolsSet: Set<@JvmSuppressWildcards AgentTool>,
    private val pluginRegistry: PluginRegistry
) {
    private val tools = mutableMapOf<String, AgentTool>()

    init {
        toolsSet.forEach { register(it) }

        // Initialize plugins
        pluginRegistry.loadPlugins()
        pluginRegistry.getPlugins().forEach { register(it) }
    }

    fun register(tool: AgentTool) {
        tools[tool.name] = tool
    }

    fun getTool(name: String): AgentTool? = tools[name]

    fun listTools(): List<AgentTool> = tools.values.toList()
}

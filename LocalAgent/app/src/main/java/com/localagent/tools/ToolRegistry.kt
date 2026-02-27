package com.localagent.tools

import com.localagent.plugins.PluginRegistry
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ToolRegistry @Inject constructor(
    accessibilityTools: AccessibilityTools,
    pluginRegistry: PluginRegistry
) {
    private val tools = mutableMapOf<String, AgentTool>()

    init {
        accessibilityTools.registerAll(this)
        pluginRegistry.getBuiltinTools().forEach { register(it) }
    }

    fun register(tool: AgentTool) {
        tools[tool.name] = tool
    }

    fun getTool(name: String): AgentTool? = tools[name]

    fun listTools(): List<AgentTool> = tools.values.toList()
}

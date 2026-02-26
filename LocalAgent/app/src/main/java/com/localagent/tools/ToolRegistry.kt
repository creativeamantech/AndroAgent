package com.localagent.tools

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ToolRegistry @Inject constructor(
    accessibilityTools: AccessibilityTools
) {
    private val tools = mutableMapOf<String, AgentTool>()

    init {
        accessibilityTools.registerAll(this)
    }

    fun register(tool: AgentTool) {
        tools[tool.name] = tool
    }

    fun getTool(name: String): AgentTool? = tools[name]

    fun listTools(): List<AgentTool> = tools.values.toList()
}

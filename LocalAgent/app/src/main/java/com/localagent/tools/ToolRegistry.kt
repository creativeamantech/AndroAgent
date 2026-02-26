package com.localagent.tools

import com.localagent.plugins.PluginLoader
import com.localagent.plugins.builtin.SpotifyTools
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ToolRegistry @Inject constructor(
    accessibilityTools: AccessibilityTools,
    visionTools: VisionTools,
    spotifyTools: SpotifyTools,
    pluginLoader: PluginLoader
) {
    private val tools = mutableMapOf<String, AgentTool>()

    init {
        accessibilityTools.registerAll(this)
        visionTools.registerAll(this)
        spotifyTools.getTools().forEach { register(it) }
        pluginLoader.loadPlugins().forEach { register(it) }
    }

    fun register(tool: AgentTool) {
        tools[tool.name] = tool
    }

    fun getTool(name: String): AgentTool? = tools[name]

    fun listTools(): List<AgentTool> = tools.values.toList()
}

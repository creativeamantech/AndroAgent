package com.localagent.agent

import com.localagent.tools.AgentTool
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PromptBuilder @Inject constructor() {
    fun buildSystemPrompt(tools: List<AgentTool>): String {
        val toolsDescription = tools.joinToString(", ") { "${it.name}: ${it.description}" }
        return """
            You are an Android device automation agent.
            You must respond ONLY in this JSON format:
            {
              "thought": "reasoning...",
              "action": {
                "tool": "<tool_name>",
                "args": { ... }
              }
            }
            Available tools: [$toolsDescription]
            Use the tool 'done' with args {"result": "summary"} when finished.
            Always use 'read_screen' first to understand the UI state.
        """.trimIndent()
    }
}

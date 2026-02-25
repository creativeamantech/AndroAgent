package com.localagent.tools

import org.json.JSONObject

interface AgentTool {
    val name: String
    val description: String
    val argsSchema: JSONObject
    suspend fun execute(args: JSONObject): ToolResult
}

data class ToolResult(val output: String)

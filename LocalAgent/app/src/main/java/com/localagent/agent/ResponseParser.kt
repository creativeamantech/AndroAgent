package com.localagent.agent

import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ResponseParser @Inject constructor() {
    fun parse(response: String): AgentResponse {
        return try {
            val json = JSONObject(response)
            val thought = json.optString("thought")
            if (json.has("action")) {
                val action = json.getJSONObject("action")
                val tool = action.optString("tool")
                val args = action.optJSONObject("args") ?: JSONObject()
                AgentAction(thought, tool, args)
            } else {
                AgentMessage(thought.ifEmpty { response }) // If no thought but valid JSON, treat as message? Or raw response?
            }
        } catch (e: Exception) {
            AgentError("Failed to parse JSON: ${e.message}")
        }
    }
}

sealed class AgentResponse
data class AgentAction(val thought: String, val tool: String, val args: JSONObject) : AgentResponse()
data class AgentMessage(val content: String) : AgentResponse()
data class AgentError(val message: String) : AgentResponse()

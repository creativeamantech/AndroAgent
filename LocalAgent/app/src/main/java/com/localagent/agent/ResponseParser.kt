package com.localagent.agent

import org.json.JSONObject
import javax.inject.Inject

class ResponseParser @Inject constructor() {

    fun parse(response: String): AgentAction? {
        try {
            // cleaner: remove markdown code blocks
            var jsonString = response.trim()
            if (jsonString.contains("```json")) {
                val start = jsonString.indexOf("```json") + 7
                val end = jsonString.indexOf("```", start)
                if (end > start) {
                    jsonString = jsonString.substring(start, end).trim()
                }
            } else if (jsonString.contains("```")) {
                 val start = jsonString.indexOf("```") + 3
                val end = jsonString.indexOf("```", start)
                if (end > start) {
                    jsonString = jsonString.substring(start, end).trim()
                }
            }

            // Attempt to find JSON object if it's embedded in text
            val firstBrace = jsonString.indexOf('{')
            val lastBrace = jsonString.lastIndexOf('}')
            if (firstBrace != -1 && lastBrace != -1 && lastBrace > firstBrace) {
                jsonString = jsonString.substring(firstBrace, lastBrace + 1)
            }

            val json = JSONObject(jsonString)
            if (json.has("action")) {
                val action = json.getJSONObject("action")
                val toolName = action.getString("tool")
                val args = action.optJSONObject("args") ?: JSONObject()
                val thought = json.optString("thought", "")
                return AgentAction(thought, toolName, args)
            }
        } catch (e: Exception) {
            // Failed to parse
        }
        return null
    }
}

data class AgentAction(
    val thought: String,
    val tool: String,
    val args: JSONObject
)

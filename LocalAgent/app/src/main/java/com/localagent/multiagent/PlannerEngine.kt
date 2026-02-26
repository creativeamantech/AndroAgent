package com.localagent.multiagent

import com.localagent.llm.LLMClient
import com.localagent.llm.Message
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.collect
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject

class PlannerEngine @Inject constructor(
    private val llmClient: LLMClient
) {
    suspend fun plan(goal: String): List<String> {
        val systemPrompt = """
            You are a task planning agent. Given a high-level user goal, decompose it into a list of atomic, sequential subtasks that an executor agent can perform one at a time.
            The executor has tools to interact with Android apps (tap, swipe, type, read screen).

            Respond ONLY as JSON: { "subtasks": ["step 1...", "step 2...", ...] }

            Example:
            Goal: "Send a message to John saying I'll be late"
            Response: { "subtasks": ["Open Messaging app", "Start new conversation", "Search for contact 'John'", "Type message 'I'll be late'", "Tap send button"] }
        """.trimIndent()

        val messages = listOf(
            Message("system", systemPrompt),
            Message("user", "Goal: $goal")
        )

        var response = ""
        llmClient.chat(messages, false).collect {
            response += it
        }

        return parsePlan(response)
    }

    private fun parsePlan(response: String): List<String> {
        val subtasks = mutableListOf<String>()
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

            val json = JSONObject(jsonString)
            val array = json.optJSONArray("subtasks") ?: JSONArray()
            for (i in 0 until array.length()) {
                subtasks.add(array.getString(i))
            }
        } catch (e: Exception) {
            // If parsing fails, return the goal as a single task
            if (subtasks.isEmpty()) {
                subtasks.add(response) // Fallback
            }
        }
        return subtasks
    }
}

package com.localagent.multiagent

import com.localagent.llm.LLMClient
import com.localagent.llm.Message
import org.json.JSONObject
import javax.inject.Inject

class PlannerEngine @Inject constructor(
    private val llmClient: LLMClient
) {
    suspend fun plan(goal: String): List<String> {
        val systemPrompt = """
            You are a task planning agent. Given a high-level user goal, decompose it into a
            list of atomic, sequential subtasks that an executor agent can perform one at a time.
            Respond ONLY as JSON: { "subtasks": ["step 1...", "step 2...", ...] }
        """.trimIndent()

        val messages = listOf(
            Message("system", systemPrompt),
            Message("user", "Goal: $goal")
        )

        var response = ""
        try {
            llmClient.chat(messages, false).collect { chunk ->
                response += chunk
            }

            // Basic parsing
            val jsonStart = response.indexOf('{')
            val jsonEnd = response.lastIndexOf('}')
            if (jsonStart != -1 && jsonEnd != -1) {
                val jsonStr = response.substring(jsonStart, jsonEnd + 1)
                val json = JSONObject(jsonStr)
                val subtasksArray = json.optJSONArray("subtasks")
                val subtasks = mutableListOf<String>()
                if (subtasksArray != null) {
                    for (i in 0 until subtasksArray.length()) {
                        subtasks.add(subtasksArray.getString(i))
                    }
                }
                return subtasks
            }
        } catch (e: Exception) {
            // Fallback or error handling
        }

        return listOf(goal) // Fallback to single task if planning fails
    }
}

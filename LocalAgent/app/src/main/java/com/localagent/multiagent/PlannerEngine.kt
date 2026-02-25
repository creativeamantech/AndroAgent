package com.localagent.multiagent

import com.localagent.llm.LLMClient
import com.localagent.llm.Message
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlannerEngine @Inject constructor(
    private val llmClient: LLMClient
) {
    suspend fun plan(goal: String): Flow<List<String>> = flow {
        val systemPrompt = """
            You are a task planning agent. Given a high-level user goal, decompose it into a
            list of atomic, sequential subtasks that an executor agent can perform one at a time.
            Respond ONLY as JSON: { "subtasks": ["step 1...", "step 2...", ...] }
        """.trimIndent()

        val history = mutableListOf(
            Message("system", systemPrompt),
            Message("user", goal)
        )

        var fullResponse = ""
        try {
            llmClient.chat(history, false).collect { chunk ->
                fullResponse += chunk
            }

            val json = JSONObject(fullResponse)
            val subtasksArray = json.optJSONArray("subtasks")
            val subtasks = mutableListOf<String>()
            if (subtasksArray != null) {
                for (i in 0 until subtasksArray.length()) {
                    subtasks.add(subtasksArray.getString(i))
                }
            }
            emit(subtasks)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }
}

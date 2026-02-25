package com.localagent.agent

import com.localagent.llm.LLMClient
import com.localagent.llm.Message
import com.localagent.tools.ToolRegistry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AgentEngine @Inject constructor(
    private val llmClient: LLMClient,
    private val toolRegistry: ToolRegistry
) {
    suspend fun run(task: String): Flow<String> = flow {
        emit("Thinking...")

        val history = mutableListOf<Message>()
        history.add(Message("system", "You are a helpful AI assistant."))
        history.add(Message("user", task))

        // Simple loop for demonstration
        var response = ""
        try {
            llmClient.chat(history, false).collect { chunk ->
                response += chunk
            }
            emit(response)
        } catch (e: Exception) {
            emit("Error: ${e.message}")
        }

        // Tool calling logic would be here (parsing JSON from response)
    }
}

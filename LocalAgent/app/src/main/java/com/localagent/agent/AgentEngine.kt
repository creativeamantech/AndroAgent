package com.localagent.agent

import com.localagent.llm.LLMClient
import com.localagent.llm.Message
import com.localagent.tools.ToolRegistry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.json.JSONObject
import javax.inject.Inject

class AgentEngine @Inject constructor(
    private val llmClient: LLMClient,
    private val toolRegistry: ToolRegistry
) {
    suspend fun run(task: String): Flow<String> = flow {
        emit("Thinking...")

        val tools = toolRegistry.listTools()
        val toolsDescription = tools.joinToString(", ") { "${it.name}: ${it.description}" }
        val systemPrompt = """
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
        """.trimIndent()

        val history = mutableListOf<Message>()
        history.add(Message("system", systemPrompt))
        history.add(Message("user", task))

        var steps = 0
        val maxSteps = 5

        while (steps < maxSteps) {
            var fullResponse = ""
            try {
                llmClient.chat(history, false).collect { chunk ->
                    fullResponse += chunk
                }
            } catch (e: Exception) {
                emit("Error communicating with LLM: ${e.message}")
                return@flow
            }

            try {
                val json = JSONObject(fullResponse)
                val thought = json.optString("thought")
                emit("Thought: $thought")

                if (json.has("action")) {
                    val action = json.getJSONObject("action")
                    val toolName = action.optString("tool")
                    val args = action.optJSONObject("args") ?: JSONObject()

                    if (toolName == "done") {
                        val result = args.optString("result")
                        emit("Done: $result")
                        return@flow
                    }

                    val tool = toolRegistry.getTool(toolName)
                    if (tool != null) {
                        emit("Executing tool: $toolName")
                        try {
                            val result = tool.execute(args)
                            val observation = "Tool '$toolName' output: ${result.output}"
                            emit("Observation: ${result.output}")

                            history.add(Message("assistant", fullResponse))
                            history.add(Message("user", observation))
                        } catch (e: Exception) {
                            val errorObs = "Tool execution failed: ${e.message}"
                            emit(errorObs)
                            history.add(Message("assistant", fullResponse))
                            history.add(Message("user", errorObs))
                        }
                    } else {
                        val errorObs = "Tool '$toolName' not found."
                        emit(errorObs)
                        history.add(Message("assistant", fullResponse))
                        history.add(Message("user", errorObs))
                    }
                } else {
                    emit("Response: $fullResponse")
                    return@flow
                }
            } catch (e: Exception) {
                emit("Failed to parse JSON response: $fullResponse")
                return@flow
            }
            steps++
        }
        emit("Max steps reached.")
    }
}

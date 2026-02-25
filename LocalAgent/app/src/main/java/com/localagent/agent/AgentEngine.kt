package com.localagent.agent

import com.localagent.llm.LLMClient
import com.localagent.llm.Message
import com.localagent.memory.MemoryRepository
import com.localagent.memory.TaskRun
import com.localagent.tools.ToolRegistry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AgentEngine @Inject constructor(
    private val llmClient: LLMClient,
    private val toolRegistry: ToolRegistry,
    private val memoryRepository: MemoryRepository,
    private val promptBuilder: PromptBuilder,
    private val responseParser: ResponseParser
) {
    suspend fun run(task: String): Flow<String> = flow {
        emit("Thinking...")

        val tools = toolRegistry.listTools()
        val systemPrompt = promptBuilder.buildSystemPrompt(tools)

        val history = mutableListOf<Message>()
        history.add(Message("system", systemPrompt))
        history.add(Message("user", task))

        var steps = 0
        val maxSteps = 10
        var outcome = "FAILURE"

        while (steps < maxSteps) {
            var fullResponse = ""
            try {
                llmClient.chat(history, false).collect { chunk ->
                    fullResponse += chunk
                }
            } catch (e: Exception) {
                emit("Error communicating with LLM: ${e.message}")
                saveTask(task, history, "ERROR")
                return@flow
            }

            val response = responseParser.parse(fullResponse)

            when (response) {
                is AgentAction -> {
                    emit("Thought: ${response.thought}")

                    if (response.tool == "done") {
                        val result = response.args.optString("result")
                        emit("Done: $result")
                        outcome = "SUCCESS"
                        saveTask(task, history, outcome)
                        return@flow
                    }

                    val tool = toolRegistry.getTool(response.tool)
                    if (tool != null) {
                        emit("Executing tool: ${response.tool}")
                        try {
                            val result = tool.execute(response.args)
                            val observation = "Tool '${response.tool}' output: ${result.output}"
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
                        val errorObs = "Tool '${response.tool}' not found."
                        emit(errorObs)
                        history.add(Message("assistant", fullResponse))
                        history.add(Message("user", errorObs))
                    }
                }
                is AgentMessage -> {
                    emit("Response: ${response.content}")
                    history.add(Message("assistant", fullResponse))
                }
                is AgentError -> {
                    emit("Failed to parse JSON response: ${response.message}")
                    // Retry or just log?
                    // history.add(Message("assistant", fullResponse))
                    // history.add(Message("user", "Invalid JSON. Please use correct format."))
                }
            }
            steps++
        }
        emit("Max steps reached.")
        saveTask(task, history, "TIMEOUT")
    }

    private suspend fun saveTask(task: String, history: List<Message>, outcome: String) {
        val stepsJson = history.joinToString("\n") { "${it.role}: ${it.content}" }
        val taskRun = TaskRun(
            task = task,
            steps = stepsJson,
            outcome = outcome,
            timestamp = System.currentTimeMillis()
        )
        memoryRepository.saveTaskRun(taskRun)
    }
}

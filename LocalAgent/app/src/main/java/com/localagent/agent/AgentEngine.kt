package com.localagent.agent

import com.localagent.accessibility.AgentAccessibilityService
import com.localagent.llm.LLMClient
import com.localagent.llm.Message
import com.localagent.tools.ToolRegistry
import com.localagent.tools.ToolResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AgentEngine @Inject constructor(
    private val llmClient: LLMClient,
    private val toolRegistry: ToolRegistry,
    private val promptBuilder: PromptBuilder,
    private val responseParser: ResponseParser
) {
    suspend fun run(task: String): Flow<String> = flow {
        emit("Starting task: $task")

        val history = mutableListOf<AgentStep>()
        val maxSteps = 15
        var currentStep = 0

        while (currentStep < maxSteps) {
            currentStep++
            emit("Step $currentStep: Reading screen...")

            // 1. Get current screen content
            val screenContent = AgentAccessibilityService.instance?.getScreenContent() ?: "{ \"error\": \"Accessibility Service not connected\" }"

            // 2. Build prompt
            val systemPrompt = promptBuilder.buildSystemPrompt()
            val userPrompt = promptBuilder.buildUserPrompt(task, screenContent, history)

            val messages = listOf(
                Message("system", systemPrompt),
                Message("user", userPrompt)
            )

            emit("Step $currentStep: Thinking...")

            // 3. Query LLM
            var llmResponse = ""
            try {
                llmClient.chat(messages, false).collect { chunk ->
                    llmResponse += chunk
                }
            } catch (e: Exception) {
                emit("Error during LLM chat: ${e.message}")
                break
            }

            // 4. Parse response
            val action = responseParser.parse(llmResponse)
            if (action == null) {
                emit("Failed to parse LLM response.")
                history.add(AgentStep("Failed to parse response", "None", "Error: Invalid JSON response"))
                continue
            }

            emit("Thought: ${action.thought}")
            emit("Action: ${action.tool} ${action.args}")

            // 5. Execute tool
            val tool = toolRegistry.getTool(action.tool)
            if (tool == null) {
                emit("Error: Tool '${action.tool}' not found.")
                history.add(AgentStep(action.thought, action.tool, "Error: Tool '${action.tool}' not found"))
                continue
            }

            if (action.tool == "done") {
                emit("Task completed: ${action.args.optString("result")}")
                break
            }

            val result = try {
                tool.execute(action.args)
            } catch (e: Exception) {
                ToolResult("Error executing tool: ${e.message}")
            }

            emit("Observation: ${result.output}")

            // 6. Update history
            history.add(AgentStep(action.thought, "${action.tool}(${action.args})", result.output))
        }

        if (currentStep >= maxSteps) {
            emit("Task failed: Max steps reached.")
        }
    }
}

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
    suspend fun run(task: String): Flow<AgentState> = flow {
        emit(AgentState.Thinking)

        val history = mutableListOf<AgentStep>()
        val maxSteps = 15
        var currentStep = 0

        while (currentStep < maxSteps) {
            currentStep++

            // 1. Get current screen content
            val screenContent = AgentAccessibilityService.instance?.getScreenContent() ?: "{ \"error\": \"Accessibility Service not connected\" }"

            // 2. Build prompt
            val systemPrompt = promptBuilder.buildSystemPrompt()
            val userPrompt = promptBuilder.buildUserPrompt(task, screenContent, history)

            val messages = listOf(
                Message("system", systemPrompt),
                Message("user", userPrompt)
            )

            // 3. Query LLM
            var llmResponse = ""
            try {
                llmClient.chat(messages, false).collect { chunk ->
                    llmResponse += chunk
                }
            } catch (e: Exception) {
                emit(AgentState.Error("Error during LLM chat: ${e.message}"))
                break
            }

            // 4. Parse response
            val action = responseParser.parse(llmResponse)
            if (action == null) {
                history.add(AgentStep("Failed to parse response", "None", "Error: Invalid JSON response"))
                continue
            }

            emit(AgentState.Acting(action.tool))

            // 5. Execute tool
            val tool = toolRegistry.getTool(action.tool)
            if (tool == null) {
                history.add(AgentStep(action.thought, action.tool, "Error: Tool '${action.tool}' not found"))
                continue
            }

            if (action.tool == "done") {
                emit(AgentState.Done(action.args.optString("result")))
                break
            }

            val result = try {
                tool.execute(action.args)
            } catch (e: Exception) {
                ToolResult("Error executing tool: ${e.message}")
            }

            // 6. Update history
            history.add(AgentStep(action.thought, "${action.tool}(${action.args})", result.output))
            emit(AgentState.Thinking)
        }

        if (currentStep >= maxSteps) {
            emit(AgentState.Error("Task failed: Max steps reached."))
        }
    }
}

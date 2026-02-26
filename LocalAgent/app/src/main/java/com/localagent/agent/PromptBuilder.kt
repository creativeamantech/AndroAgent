package com.localagent.agent

import com.localagent.tools.ToolRegistry
import javax.inject.Inject

class PromptBuilder @Inject constructor(
    private val toolRegistry: ToolRegistry
) {

    fun buildSystemPrompt(): String {
        val toolsDesc = toolRegistry.listTools().joinToString("\n") { tool ->
            "- ${tool.name}: ${tool.description}\n  Args: ${tool.argsSchema}"
        }

        return """
            You are an Android device automation agent. Your goal is to complete user tasks by controlling the Android device through a set of tools.

            Available tools:
            $toolsDesc

            At each step you will receive:
            - The user's task
            - The current screen content (accessibility tree)
            - Your action history so far

            You must respond ONLY in this JSON format:
            {
              "thought": "Your reasoning about what to do next",
              "action": {
                "tool": "<tool_name>",
                "args": { ...tool arguments... }
              }
            }

            Rules:
            - Always examine the screen content before acting.
            - Prefer read_screen() for speed; use vision_read_screen() if tree is empty or blocked.
            - Use done() when the task is fully completed.
            - Never guess coordinates; derive them from screen content.
            - If stuck after 3 retries on the same step, call done() with an error explanation.
        """.trimIndent()
    }

    fun buildUserPrompt(task: String, screenContent: String, history: List<AgentStep>): String {
        val historyStr = history.joinToString("\n\n") { step ->
            "Thought: ${step.thought}\nAction: ${step.action}\nObservation: ${step.observation}"
        }

        return """
            Task: $task

            History:
            $historyStr

            Current Screen State:
            $screenContent

            What is your next step? (Respond with JSON only)
        """.trimIndent()
    }
}

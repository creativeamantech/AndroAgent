package com.localagent.agent

import com.localagent.tools.AgentTool
import com.localagent.tools.ToolResult
import org.json.JSONObject
import org.junit.Assert.assertTrue
import org.junit.Test

class PromptBuilderTest {
    @Test
    fun testBuildSystemPromptContainsTools() {
        val tools = listOf(
            object : AgentTool {
                override val name = "test_tool"
                override val description = "This is a test tool"
                override val argsSchema = JSONObject("{}")
                override suspend fun execute(args: JSONObject) = ToolResult("")
            }
        )
        val prompt = PromptBuilder().buildSystemPrompt(tools)
        assertTrue(prompt.contains("test_tool"))
        assertTrue(prompt.contains("This is a test tool"))
    }
}

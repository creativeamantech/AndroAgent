package com.localagent.tools

import com.localagent.accessibility.AccessibilityController
import org.json.JSONObject
import javax.inject.Inject

class ReadScreenTool @Inject constructor(private val controller: AccessibilityController) : AgentTool {
    override val name = "read_screen"
    override val description = "Read the current screen content as a JSON tree"
    override val argsSchema = JSONObject("""
        {
            "type": "object",
            "properties": {},
            "additionalProperties": false
        }
    """)

    override suspend fun execute(args: JSONObject): ToolResult {
        val content = controller.getScreenContent()
        return ToolResult(content)
    }
}

package com.localagent.plugins

import com.localagent.tools.AgentTool
import com.localagent.tools.ToolResult
import org.json.JSONObject

class HomeAssistantPlugin : AgentTool {
    override val name = "home_assistant_control"
    override val description = "Control smart home devices via Home Assistant"
    override val argsSchema = JSONObject("""
        {
            "type": "object",
            "properties": {
                "entity_id": { "type": "string" },
                "action": { "type": "string", "enum": ["turn_on", "turn_off"] }
            },
            "required": ["entity_id", "action"]
        }
    """)

    override suspend fun execute(args: JSONObject): ToolResult {
        val entityId = args.optString("entity_id")
        val action = args.optString("action")
        return ToolResult("Executed $action on $entityId (Stub)")
    }
}

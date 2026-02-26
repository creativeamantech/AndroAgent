package com.localagent.tools

import com.localagent.accessibility.AgentAccessibilityService
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccessibilityTools @Inject constructor() {

    val readScreenTool = object : AgentTool {
        override val name = "read_screen"
        override val description = "Read the current screen content as a JSON tree."
        override val argsSchema = JSONObject("""{"type": "object", "properties": {}}""")

        override suspend fun execute(args: JSONObject): ToolResult {
            val service = AgentAccessibilityService.instance
            return if (service != null) {
                ToolResult(service.getScreenContent())
            } else {
                ToolResult("Error: Accessibility Service not connected.")
            }
        }
    }

    val tapTool = object : AgentTool {
        override val name = "tap"
        override val description = "Tap at the specified coordinates (x, y)."
        override val argsSchema = JSONObject("""{
            "type": "object",
            "properties": {
                "x": {"type": "integer"},
                "y": {"type": "integer"}
            },
            "required": ["x", "y"]
        }""")

        override suspend fun execute(args: JSONObject): ToolResult {
            val service = AgentAccessibilityService.instance ?: return ToolResult("Error: Service not connected.")
            val x = args.optInt("x")
            val y = args.optInt("y")
            val success = service.tap(x, y)
            return if (success) ToolResult("Tapped at ($x, $y)") else ToolResult("Failed to tap at ($x, $y)")
        }
    }

    val swipeTool = object : AgentTool {
        override val name = "swipe"
        override val description = "Swipe from (x1, y1) to (x2, y2)."
        override val argsSchema = JSONObject("""{
            "type": "object",
            "properties": {
                "x1": {"type": "integer"},
                "y1": {"type": "integer"},
                "x2": {"type": "integer"},
                "y2": {"type": "integer"}
            },
            "required": ["x1", "y1", "x2", "y2"]
        }""")

        override suspend fun execute(args: JSONObject): ToolResult {
            val service = AgentAccessibilityService.instance ?: return ToolResult("Error: Service not connected.")
            val x1 = args.optInt("x1")
            val y1 = args.optInt("y1")
            val x2 = args.optInt("x2")
            val y2 = args.optInt("y2")
            val success = service.swipe(x1, y1, x2, y2)
            return if (success) ToolResult("Swiped from ($x1, $y1) to ($x2, $y2)") else ToolResult("Failed to swipe.")
        }
    }

    val typeTextTool = object : AgentTool {
        override val name = "type_text"
        override val description = "Type text into the currently focused input field."
        override val argsSchema = JSONObject("""{
            "type": "object",
            "properties": {
                "text": {"type": "string"}
            },
            "required": ["text"]
        }""")

        override suspend fun execute(args: JSONObject): ToolResult {
            val service = AgentAccessibilityService.instance ?: return ToolResult("Error: Service not connected.")
            val text = args.optString("text")
            val success = service.typeText(text)
            return if (success) ToolResult("Typed: $text") else ToolResult("Failed to type text (no focused input?).")
        }
    }

    val pressKeyTool = object : AgentTool {
        override val name = "press_key"
        override val description = "Press a global key (HOME, BACK, RECENTS, NOTIFICATIONS)."
        override val argsSchema = JSONObject("""{
            "type": "object",
            "properties": {
                "key": {"type": "string", "enum": ["HOME", "BACK", "RECENTS", "NOTIFICATIONS"]}
            },
            "required": ["key"]
        }""")

        override suspend fun execute(args: JSONObject): ToolResult {
            val service = AgentAccessibilityService.instance ?: return ToolResult("Error: Service not connected.")
            val key = args.optString("key").uppercase()
            val success = when (key) {
                "HOME" -> service.home()
                "BACK" -> service.back()
                "RECENTS" -> service.recents()
                "NOTIFICATIONS" -> service.notifications()
                else -> return ToolResult("Unknown key: $key")
            }
            return if (success) ToolResult("Pressed key: $key") else ToolResult("Failed to press key: $key")
        }
    }

    val doneTool = object : AgentTool {
        override val name = "done"
        override val description = "Signal that the task is complete."
        override val argsSchema = JSONObject("""{
            "type": "object",
            "properties": {
                "result": {"type": "string"}
            },
            "required": ["result"]
        }""")

        override suspend fun execute(args: JSONObject): ToolResult {
            return ToolResult("Task Completed: " + args.optString("result"))
        }
    }

    fun registerAll(registry: ToolRegistry) {
        registry.register(readScreenTool)
        registry.register(tapTool)
        registry.register(swipeTool)
        registry.register(typeTextTool)
        registry.register(pressKeyTool)
        registry.register(doneTool)
    }
}

package com.localagent.tools

import com.localagent.accessibility.AccessibilityController
import org.json.JSONObject
import javax.inject.Inject

class TapTool @Inject constructor(private val controller: AccessibilityController) : AgentTool {
    override val name = "tap"
    override val description = "Tap at specific coordinates (x, y)"
    override val argsSchema = JSONObject("""
        {
            "type": "object",
            "properties": {
                "x": { "type": "number" },
                "y": { "type": "number" }
            },
            "required": ["x", "y"]
        }
    """)

    override suspend fun execute(args: JSONObject): ToolResult {
        val x = args.optDouble("x", 0.0).toFloat()
        val y = args.optDouble("y", 0.0).toFloat()
        controller.tap(x, y)
        return ToolResult("Tapped at ($x, $y)")
    }
}

class SwipeTool @Inject constructor(private val controller: AccessibilityController) : AgentTool {
    override val name = "swipe"
    override val description = "Swipe from (x1, y1) to (x2, y2)"
    override val argsSchema = JSONObject("""
        {
            "type": "object",
            "properties": {
                "x1": { "type": "number" },
                "y1": { "type": "number" },
                "x2": { "type": "number" },
                "y2": { "type": "number" }
            },
            "required": ["x1", "y1", "x2", "y2"]
        }
    """)

    override suspend fun execute(args: JSONObject): ToolResult {
        val x1 = args.optDouble("x1", 0.0).toFloat()
        val y1 = args.optDouble("y1", 0.0).toFloat()
        val x2 = args.optDouble("x2", 0.0).toFloat()
        val y2 = args.optDouble("y2", 0.0).toFloat()
        controller.swipe(x1, y1, x2, y2)
        return ToolResult("Swiped from ($x1, $y1) to ($x2, $y2)")
    }
}

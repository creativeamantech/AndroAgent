package com.localagent.tools

import com.localagent.accessibility.AccessibilityController
import com.localagent.llm.VLMClient
import kotlinx.coroutines.suspendCancellableCoroutine
import org.json.JSONObject
import javax.inject.Inject
import kotlin.coroutines.resume

class VisionReadScreenTool @Inject constructor(
    private val controller: AccessibilityController,
    private val vlmClient: VLMClient
) : AgentTool {
    override val name = "vision_read_screen"
    override val description = "Take a screenshot and analyze UI elements using VLM"
    override val argsSchema = JSONObject("{}")

    override suspend fun execute(args: JSONObject): ToolResult {
        val screenshotBase64: String? = suspendCancellableCoroutine { cont ->
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                controller.takeScreenshot { base64 ->
                    if (cont.isActive) cont.resume(base64)
                }
            } else {
                 if (cont.isActive) cont.resume(null)
            }
        }

        if (screenshotBase64 == null) {
            return ToolResult("Failed to take screenshot. Ensure Android 11+ and Accessibility Service enabled.")
        }

        val prompt = "You are a UI analysis assistant. Given a screenshot, return ONLY a JSON object listing all visible interactive elements with their text, type (button/input/text/image), and bounding box as [x, y, width, height] in screen pixels. Format: { \"elements\": [{ \"text\": \"...\", \"type\": \"button\", \"bounds\": [x,y,w,h] }] }"

        var result = ""
        try {
            vlmClient.analyzeImage(prompt, screenshotBase64).collect { chunk ->
                result += chunk
            }
        } catch (e: Exception) {
            return ToolResult("VLM Analysis failed: ${e.message}")
        }

        return ToolResult(result)
    }
}

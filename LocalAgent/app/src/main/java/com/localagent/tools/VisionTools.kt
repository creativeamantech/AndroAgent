package com.localagent.tools

import android.graphics.Bitmap
import android.util.Base64
import com.localagent.accessibility.AgentAccessibilityService
import com.localagent.llm.VLMClient
import kotlinx.coroutines.CompletableDeferred
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VisionTools @Inject constructor(
    private val vlmClient: VLMClient
) {

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
    }

    val takeScreenshotTool = object : AgentTool {
        override val name = "take_screenshot"
        override val description = "Capture and return a screenshot (base64 PNG)."
        override val argsSchema = JSONObject("""{"type": "object", "properties": {}}""")

        override suspend fun execute(args: JSONObject): ToolResult {
            val service = AgentAccessibilityService.instance ?: return ToolResult("Error: Service not connected.")
            val deferred = CompletableDeferred<String>()

            service.takeScreenshot { bitmap ->
                if (bitmap != null) {
                    val base64 = bitmapToBase64(bitmap)
                    deferred.complete(base64)
                } else {
                    deferred.complete("Error: Failed to take screenshot.")
                }
            }

            return ToolResult(deferred.await())
        }
    }

    val visionReadScreenTool = object : AgentTool {
        override val name = "vision_read_screen"
        override val description = "Capture screenshot -> send to VLM -> get visual element list."
        override val argsSchema = JSONObject("""{"type": "object", "properties": {}}""")

        override suspend fun execute(args: JSONObject): ToolResult {
            val service = AgentAccessibilityService.instance ?: return ToolResult("Error: Service not connected.")
            val deferred = CompletableDeferred<String>()

            service.takeScreenshot { bitmap ->
                if (bitmap != null) {
                    val base64 = bitmapToBase64(bitmap)
                    deferred.complete(base64)
                } else {
                    deferred.complete("")
                }
            }

            val base64 = deferred.await()
            if (base64.isEmpty()) return ToolResult("Error: Failed to take screenshot.")

            var vlmResponse = ""
            try {
                vlmClient.analyzeScreenshot(base64).collect {
                    vlmResponse += it
                }
            } catch (e: Exception) {
                return ToolResult("Error connecting to VLM: ${e.message}")
            }

            return ToolResult(vlmResponse)
        }
    }

    fun registerAll(registry: ToolRegistry) {
        registry.register(takeScreenshotTool)
        registry.register(visionReadScreenTool)
    }
}

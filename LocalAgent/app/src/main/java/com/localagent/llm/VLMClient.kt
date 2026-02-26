package com.localagent.llm

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VLMClient @Inject constructor() {

    private val client = OkHttpClient()
    private val baseUrl = "http://10.0.2.2:11434"

    fun analyzeScreenshot(base64Image: String): Flow<String> = flow {
        val json = JSONObject()
        json.put("model", "llava") // Or minicpm-v
        json.put("prompt", """
            You are a UI analysis assistant. Given a screenshot, return ONLY a JSON object listing all visible interactive elements with their text, type (button/input/text/image), and bounding box as [x, y, width, height] in screen pixels.
            Format: { "elements": [{ "text": "...", "type": "button", "bounds": [x,y,w,h] }] }
        """.trimIndent())

        val images = JSONArray()
        images.put(base64Image)
        json.put("images", images)
        json.put("stream", false)
        json.put("format", "json")

        val body = json.toString().toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url("$baseUrl/api/generate") // LLaVA uses generate endpoint usually
            .post(body)
            .build()

        try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val jsonResponse = JSONObject(response.body?.string() ?: "")
                emit(jsonResponse.optString("response", ""))
            } else {
                emit("Error: ${response.code} ${response.message}")
            }
        } catch (e: Exception) {
            emit("Error: ${e.message}")
        }
    }
}

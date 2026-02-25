package com.localagent.llm

import com.localagent.data.repository.SettingsRepository
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

class OllamaVLMClient @Inject constructor(
    private val settingsRepository: SettingsRepository
) : VLMClient {

    private val client = OkHttpClient()
    private val baseUrl: String
        get() = settingsRepository.ollamaBaseUrl

    override fun isAvailable(): Boolean {
        return try {
            val request = Request.Builder().url("$baseUrl/api/tags").build()
            client.newCall(request).execute().isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun analyzeImage(prompt: String, imageBase64: String): Flow<String> = flow {
        val json = JSONObject()
        json.put("model", "llava") // Default VLM model, hardcoded for now or configurable via settings
        json.put("prompt", prompt)
        json.put("stream", true)

        val imagesArray = JSONArray()
        imagesArray.put(imageBase64)
        json.put("images", imagesArray)

        val body = json.toString().toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url("$baseUrl/api/generate") // Vision uses generate endpoint
            .post(body)
            .build()

        try {
            val response = client.newCall(request).execute()
            val source = response.body?.byteStream()
            if (source != null) {
                val reader = BufferedReader(InputStreamReader(source))
                var line: String? = reader.readLine()
                while (line != null) {
                    val jsonResponse = JSONObject(line)
                    if (jsonResponse.has("response")) {
                        val content = jsonResponse.getString("response")
                        emit(content)
                    }
                    if (jsonResponse.optBoolean("done", false)) {
                        break
                    }
                    line = reader.readLine()
                }
            }
        } catch (e: Exception) {
            emit("Error: ${e.message}")
        }
    }
}

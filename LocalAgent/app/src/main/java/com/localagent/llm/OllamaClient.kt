package com.localagent.llm

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONObject
import javax.inject.Inject
import java.io.BufferedReader
import java.io.InputStreamReader

class OllamaClient @Inject constructor() : LLMClient {

    private val client = OkHttpClient()
    // Use 10.0.2.2 for emulator to access host machine's localhost
    private val baseUrl = "http://10.0.2.2:11434"

    override val backendName: String = "Ollama"

    override fun isAvailable(): Boolean {
        // Simple check to see if we can reach the server
        return try {
            val request = Request.Builder().url("$baseUrl/api/tags").build()
            client.newCall(request).execute().isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun chat(messages: List<Message>, stream: Boolean): Flow<String> = flow {
        val json = JSONObject()
        json.put("model", "llama3") // Default model
        json.put("messages", messages.map {
            JSONObject().apply {
                put("role", it.role)
                put("content", it.content)
            }
        })
        json.put("stream", stream)
        // json.put("format", "json") // Optional: force JSON mode if supported

        val body = json.toString().toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url("$baseUrl/api/chat")
            .post(body)
            .build()

        try {
            val response = client.newCall(request).execute()
            val source = response.body?.byteStream()
            if (source != null) {
                val reader = BufferedReader(InputStreamReader(source))
                var line: String? = reader.readLine()
                while (line != null) {
                    try {
                        val jsonResponse = JSONObject(line)
                        if (jsonResponse.has("message")) {
                            val messageContent = jsonResponse.getJSONObject("message").optString("content", "")
                            emit(messageContent)
                        }
                        if (jsonResponse.optBoolean("done", false)) {
                            break
                        }
                    } catch (e: Exception) {
                        // ignore parse errors for partial lines
                    }
                    line = reader.readLine()
                }
            }
        } catch (e: Exception) {
            emit("Error: ${e.message}")
        }
    }
}

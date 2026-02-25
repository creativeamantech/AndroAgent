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
import javax.inject.Singleton

@Singleton
class OllamaModelManager @Inject constructor(
    private val settingsRepository: SettingsRepository
) {

    private val client = OkHttpClient()
    private val baseUrl: String
        get() = settingsRepository.ollamaBaseUrl

    suspend fun listModels(): List<ModelInfo> {
        return try {
            val request = Request.Builder().url("$baseUrl/api/tags").build()
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) return emptyList()

            val jsonStr = response.body?.string() ?: "{}"
            val json = JSONObject(jsonStr)
            val models = mutableListOf<ModelInfo>()
            val modelsArray = json.optJSONArray("models") ?: JSONArray()
            for (i in 0 until modelsArray.length()) {
                val modelJson = modelsArray.getJSONObject(i)
                models.add(
                    ModelInfo(
                        name = modelJson.optString("name"),
                        size = modelJson.optLong("size"),
                        digest = modelJson.optString("digest")
                    )
                )
            }
            models
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun pullModel(modelName: String): Flow<PullProgress> = flow {
        val json = JSONObject()
        json.put("name", modelName)
        json.put("stream", true)

        val body = json.toString().toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url("$baseUrl/api/pull")
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
                    val status = jsonResponse.optString("status")
                    val completed = jsonResponse.optLong("completed", 0)
                    val total = jsonResponse.optLong("total", 0)
                    emit(PullProgress(status, completed, total))

                    if (status == "success") break

                    line = reader.readLine()
                }
            }
        } catch (e: Exception) {
            emit(PullProgress("Error: ${e.message}", 0, 0))
        }
    }

    suspend fun deleteModel(modelName: String): Boolean {
        val json = JSONObject()
        json.put("name", modelName)
        val body = json.toString().toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url("$baseUrl/api/delete")
            .method("DELETE", body)
            .build()

        return try {
            client.newCall(request).execute().isSuccessful
        } catch (e: Exception) {
            false
        }
    }
}

data class ModelInfo(val name: String, val size: Long, val digest: String)
data class PullProgress(val status: String, val completed: Long, val total: Long)

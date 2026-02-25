package com.localagent.llm

import kotlinx.coroutines.flow.Flow

interface VLMClient {
    suspend fun analyzeImage(prompt: String, imageBase64: String): Flow<String>
    fun isAvailable(): Boolean
}

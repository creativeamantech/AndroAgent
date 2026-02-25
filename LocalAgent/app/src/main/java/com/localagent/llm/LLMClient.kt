package com.localagent.llm

import kotlinx.coroutines.flow.Flow

interface LLMClient {
    suspend fun chat(messages: List<Message>, stream: Boolean): Flow<String>
    fun isAvailable(): Boolean
    val backendName: String
}

data class Message(
    val role: String,
    val content: String
)

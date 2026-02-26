package com.localagent.llm

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LLMClientFactory @Inject constructor(
    private val ollamaClient: OllamaClient,
    private val geminiNanoClient: GeminiNanoClient
) {
    fun getClient(type: LLMBackend): LLMClient {
        return when (type) {
            LLMBackend.OLLAMA -> ollamaClient
            LLMBackend.GEMINI_NANO -> geminiNanoClient
            else -> ollamaClient // Default
        }
    }
}

enum class LLMBackend {
    OLLAMA,
    GEMINI_NANO,
    LLAMA_CPP,
    MEDIA_PIPE
}

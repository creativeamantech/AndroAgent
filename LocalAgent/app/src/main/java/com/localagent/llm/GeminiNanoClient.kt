package com.localagent.llm

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeminiNanoClient @Inject constructor(
    @ApplicationContext private val context: Context
) : LLMClient {

    override val backendName: String = "Gemini Nano (On-Device)"

    // Attempt to use Google AI Edge SDK (AICore) via reflection to avoid build errors if dependency is missing.
    // Dependency: implementation("com.google.ai.edge.aicore:aicore:1.0.0-alpha01")

    override fun isAvailable(): Boolean {
        // Check for AICore system service presence
        // This is a heuristic.
        return try {
            val packageManager = context.packageManager
            packageManager.getPackageInfo("com.google.android.aicore", 0)
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun chat(messages: List<Message>, stream: Boolean): Flow<String> = flow {
        if (!isAvailable()) {
            emit("Error: Gemini Nano (AICore) is not installed or enabled on this device. Please use a supported device (e.g. Pixel 8 Pro, S24) and enable AICore.")
            return@flow
        }

        // Stub for actual AICore interaction
        // Since we cannot compile against the closed-source/early-access SDK without the artifact:
        /*
        val model = com.google.ai.edge.aicore.GenerativeModel(
            modelName = "gemini-nano",
            context = context
        )
        val chat = model.startChat()
        val response = chat.sendMessage(messages.last().content)
        emit(response.text ?: "")
        */

        emit("Error: AICore detected, but SDK bindings are missing in this build. Uncomment the code in GeminiNanoClient.kt and add the 'aicore' dependency to build.gradle.kts to enable.")
    }
}

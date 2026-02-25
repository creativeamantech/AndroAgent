package com.localagent.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localagent.agent.AgentEngine
import com.localagent.llm.Message
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val agentEngine: AgentEngine
) : ViewModel() {

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages = _messages.asStateFlow()

    fun sendMessage(text: String) {
        val userMessage = Message("user", text)
        _messages.value += userMessage

        viewModelScope.launch {
            val responseFlow = agentEngine.run(text)
            var agentResponse = ""
            // Add a placeholder message for streaming
            val agentMessageIndex = _messages.value.size
            _messages.value += Message("assistant", "")

            responseFlow.collect { chunk ->
                agentResponse = chunk // In real streaming, append chunk if implemented that way
                // Update the last message
                val currentMessages = _messages.value.toMutableList()
                if (currentMessages.size > agentMessageIndex) {
                     currentMessages[agentMessageIndex] = Message("assistant", agentResponse)
                     _messages.value = currentMessages
                }
            }
        }
    }
}

package com.localagent.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localagent.agent.AgentEngine
import com.localagent.data.repository.SettingsRepository
import com.localagent.llm.Message
import com.localagent.multiagent.AgentOrchestrator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val agentEngine: AgentEngine,
    private val agentOrchestrator: AgentOrchestrator,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages = _messages.asStateFlow()

    fun sendMessage(text: String) {
        val userMessage = Message("user", text)
        val currentMessages = _messages.value.toMutableList()
        currentMessages.add(userMessage)
        _messages.value = currentMessages

        viewModelScope.launch {
            val isMultiAgent = settingsRepository.isMultiAgentMode

            val responseFlow = if (isMultiAgent) {
                agentOrchestrator.run(text)
            } else {
                agentEngine.run(text)
            }

            // Create initial assistant message
            var agentResponse = ""
            val newMessages = _messages.value.toMutableList()
            // Placeholder message to be updated
            newMessages.add(Message("assistant", "..."))
            _messages.value = newMessages
            val agentMessageIndex = newMessages.lastIndex

            responseFlow.collect { chunk ->
                if (agentResponse.isEmpty()) {
                    agentResponse = chunk
                } else {
                    agentResponse += "\n" + chunk
                }

                val updatedMessages = _messages.value.toMutableList()
                if (updatedMessages.size > agentMessageIndex) {
                    updatedMessages[agentMessageIndex] = Message("assistant", agentResponse)
                    _messages.value = updatedMessages
                }
            }
        }
    }
}

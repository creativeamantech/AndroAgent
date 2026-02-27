package com.localagent.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localagent.agent.AgentEngine
import com.localagent.agent.AgentState
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

    private val _state = MutableStateFlow<AgentState>(AgentState.Idle)
    val state = _state.asStateFlow()

    fun sendMessage(text: String) {
        val userMessage = Message("user", text)
        _messages.value += userMessage

        viewModelScope.launch {
            agentEngine.run(text).collect { agentState ->
                _state.value = agentState
                when (agentState) {
                    is AgentState.Done -> {
                        val assistantMessage = Message("assistant", agentState.result)
                        _messages.value += assistantMessage
                    }
                    is AgentState.Error -> {
                        val errorMessage = Message("system", "Error: ${agentState.message}")
                        _messages.value += errorMessage
                    }
                    else -> {
                        // Handle other states (Thinking, Acting) if needed in UI
                    }
                }
            }
        }
    }
}

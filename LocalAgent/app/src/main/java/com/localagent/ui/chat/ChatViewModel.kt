package com.localagent.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localagent.multiagent.AgentOrchestrator
import com.localagent.multiagent.OrchestratorState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val orchestrator: AgentOrchestrator
) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    val orchestratorState: StateFlow<OrchestratorState> = orchestrator.state

    fun sendMessage(text: String) {
        _messages.value += ChatMessage(text, true)

        viewModelScope.launch {
            orchestrator.executeGoal(text)
        }
    }
}

data class ChatMessage(
    val text: String,
    val isUser: Boolean
)

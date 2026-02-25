package com.localagent.agent

sealed class AgentState {
    object Idle : AgentState()
    object Thinking : AgentState()
    data class Acting(val tool: String) : AgentState()
    data class Done(val result: String) : AgentState()
    data class Error(val message: String) : AgentState()
}

package com.localagent.multiagent

import com.localagent.agent.AgentEngine
import com.localagent.agent.AgentState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ExecutorEngine @Inject constructor(
    private val agentEngine: AgentEngine
) {
    suspend fun execute(subtask: String): Flow<AgentState> {
        // Reuse the single-agent ReAct loop for subtasks
        return agentEngine.run(subtask)
    }
}

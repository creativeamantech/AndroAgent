package com.localagent.multiagent

import com.localagent.agent.AgentEngine
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExecutorEngine @Inject constructor(
    private val agentEngine: AgentEngine
) {
    suspend fun execute(subtask: String): Flow<String> {
        return agentEngine.run(subtask)
    }
}

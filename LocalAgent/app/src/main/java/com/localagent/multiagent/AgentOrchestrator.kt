package com.localagent.multiagent

import com.localagent.agent.AgentState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AgentOrchestrator @Inject constructor(
    private val plannerEngine: PlannerEngine,
    private val executorEngine: ExecutorEngine
) {
    suspend fun execute(goal: String): Flow<AgentState> = flow {
        emit(AgentState.Thinking)

        // 1. Plan
        val subtasks = plannerEngine.plan(goal)
        if (subtasks.isEmpty()) {
            emit(AgentState.Error("Failed to generate a plan."))
            return@flow
        }

        // 2. Execute sequentially
        for (subtask in subtasks) {
            emit(AgentState.Thinking) // Indicate moving to next subtask

            var failed = false
            executorEngine.execute(subtask).collect { state ->
                emit(state)
                if (state is AgentState.Error) {
                    failed = true
                }
            }

            if (failed) {
                // Ideally trigger replanning here
                emit(AgentState.Error("Subtask failed: $subtask"))
                return@flow
            }
        }

        emit(AgentState.Done("All subtasks completed."))
    }
}

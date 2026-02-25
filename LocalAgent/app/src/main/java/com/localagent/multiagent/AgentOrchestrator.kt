package com.localagent.multiagent

import com.localagent.agent.AgentEngine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AgentOrchestrator @Inject constructor(
    private val plannerEngine: PlannerEngine,
    private val executorEngine: ExecutorEngine,
    private val agentEngine: AgentEngine
) {
    suspend fun run(goal: String): Flow<String> = flow {
        emit("Planning task: $goal...")

        var subtasks = emptyList<String>()
        try {
            plannerEngine.plan(goal).collect { plan ->
                subtasks = plan
            }
        } catch (e: Exception) {
            emit("Planning failed: ${e.message}")
        }

        if (subtasks.isEmpty()) {
            emit("Could not create a plan. Running as single agent...")
            // Fallback to single agent execution
            agentEngine.run(goal).collect { emit(it) }
            return@flow
        }

        emit("Plan created: ${subtasks.joinToString(", ")}")

        for ((index, subtask) in subtasks.withIndex()) {
            emit("Executing step ${index + 1}: $subtask")
            executorEngine.execute(subtask).collect { output ->
                emit(output)
            }
        }
        emit("All steps completed.")
    }
}

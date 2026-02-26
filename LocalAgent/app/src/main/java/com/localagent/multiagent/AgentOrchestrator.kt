package com.localagent.multiagent

import com.localagent.agent.AgentState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AgentOrchestrator @Inject constructor(
    private val plannerEngine: PlannerEngine,
    private val executorEngine: ExecutorEngine
) {

    private val _state = MutableStateFlow<OrchestratorState>(OrchestratorState())
    val state: StateFlow<OrchestratorState> = _state.asStateFlow()

    suspend fun executeGoal(goal: String) {
        _state.value = _state.value.copy(isPlanning = true, currentActivity = "Planning...")

        try {
            val plan = plannerEngine.plan(goal)
            if (plan.isEmpty()) {
                _state.value = _state.value.copy(
                    isPlanning = false,
                    currentActivity = "Planning failed.",
                    subtasks = emptyList()
                )
                return
            }

            val subtasks = plan.mapIndexed { index, desc ->
                Subtask(index, desc, SubtaskStatus.PENDING)
            }

            _state.value = _state.value.copy(
                isPlanning = false,
                currentActivity = "Starting execution...",
                subtasks = subtasks
            )

            for (subtask in subtasks) {
                updateSubtaskStatus(subtask.id, SubtaskStatus.RUNNING)
                _state.value = _state.value.copy(currentActivity = "Running: ${subtask.description}")

                var success = false
                try {
                    executorEngine.run(subtask.description).collect { update ->
                        // In a real app, parse update to show detailed progress
                        // For now, just log or show last update as activity
                        if (update.startsWith("Subtask completed")) {
                            success = true
                        } else if (update.startsWith("Subtask failed")) {
                            success = false
                        }
                        // _state.value = _state.value.copy(currentActivity = update) // Too noisy?
                    }
                } catch (e: Exception) {
                    success = false
                }

                if (success) {
                    updateSubtaskStatus(subtask.id, SubtaskStatus.COMPLETED)
                } else {
                    updateSubtaskStatus(subtask.id, SubtaskStatus.FAILED)
                    _state.value = _state.value.copy(currentActivity = "Failed at: ${subtask.description}")
                    break // Stop on failure?
                }
            }

            _state.value = _state.value.copy(currentActivity = "Goal completed.")

        } catch (e: Exception) {
            _state.value = _state.value.copy(currentActivity = "Error: ${e.message}")
        }
    }

    private fun updateSubtaskStatus(id: Int, status: SubtaskStatus) {
        val currentSubtasks = _state.value.subtasks.toMutableList()
        val index = currentSubtasks.indexOfFirst { it.id == id }
        if (index != -1) {
            currentSubtasks[index] = currentSubtasks[index].copy(status = status)
            _state.value = _state.value.copy(subtasks = currentSubtasks)
        }
    }
}

data class OrchestratorState(
    val isPlanning: Boolean = false,
    val currentActivity: String = "",
    val subtasks: List<Subtask> = emptyList()
)

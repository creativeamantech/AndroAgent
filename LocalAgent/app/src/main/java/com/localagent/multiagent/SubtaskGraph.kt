package com.localagent.multiagent

data class SubtaskGraph(
    val goal: String,
    val subtasks: List<Subtask>
)

data class Subtask(
    val id: Int,
    val description: String,
    var status: SubtaskStatus = SubtaskStatus.PENDING,
    var result: String? = null
)

enum class SubtaskStatus {
    PENDING,
    RUNNING,
    COMPLETED,
    FAILED
}

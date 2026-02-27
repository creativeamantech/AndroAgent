package com.localagent.memory

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "task_runs")
data class TaskRun(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val task: String,
    val steps: String, // JSON serialized List<AgentStep>
    val outcome: String, // SUCCESS, FAILURE, PARTIAL
    val userRating: Int? = null,
    val durationMs: Long = 0,
    val timestamp: Long = System.currentTimeMillis()
)

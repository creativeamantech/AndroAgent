package com.localagent.memory

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity
data class TaskRun(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val task: String,
    val steps: String, // JSON serialized
    val outcome: String,
    val timestamp: Long
)

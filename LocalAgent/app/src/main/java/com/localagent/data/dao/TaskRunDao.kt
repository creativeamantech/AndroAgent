package com.localagent.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.localagent.memory.TaskRun
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskRunDao {
    @Insert
    suspend fun insert(taskRun: TaskRun)

    @Query("SELECT * FROM task_runs ORDER BY timestamp DESC")
    fun getAll(): Flow<List<TaskRun>>

    @Query("SELECT * FROM task_runs WHERE task LIKE :query ORDER BY timestamp DESC LIMIT 3")
    suspend fun searchTasks(query: String): List<TaskRun>
}

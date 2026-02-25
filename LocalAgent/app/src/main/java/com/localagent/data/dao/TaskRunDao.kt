package com.localagent.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.localagent.memory.TaskRun
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskRunDao {
    @Query("SELECT * FROM taskrun ORDER BY timestamp DESC")
    fun getAll(): Flow<List<TaskRun>>

    @Insert
    suspend fun insert(taskRun: TaskRun)
}

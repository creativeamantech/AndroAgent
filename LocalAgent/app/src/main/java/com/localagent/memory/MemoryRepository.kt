package com.localagent.memory

import com.localagent.data.dao.TaskRunDao
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class MemoryRepository @Inject constructor(
    private val taskRunDao: TaskRunDao
) {
    suspend fun saveTaskRun(taskRun: TaskRun) {
        taskRunDao.insert(taskRun)
    }

    fun getAllTaskRuns(): Flow<List<TaskRun>> {
        return taskRunDao.getAll()
    }

    suspend fun getSimilarTasks(query: String): List<TaskRun> {
        // Basic keyword search implementation
        // ideally use FTS or vector embeddings
        return taskRunDao.searchTasks("%$query%")
    }
}

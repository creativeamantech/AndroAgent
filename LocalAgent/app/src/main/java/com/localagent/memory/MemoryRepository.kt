package com.localagent.memory

import com.localagent.data.dao.TaskRunDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MemoryRepository @Inject constructor(
    private val taskRunDao: TaskRunDao
) {
    fun getAllTaskRuns(): Flow<List<TaskRun>> {
        return taskRunDao.getAll()
    }

    suspend fun saveTaskRun(taskRun: TaskRun) {
        taskRunDao.insert(taskRun)
    }
}

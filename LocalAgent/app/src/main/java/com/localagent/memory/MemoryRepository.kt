package com.localagent.memory

import com.localagent.data.dao.TaskRunDao
import com.localagent.privacy.EncryptionManager
import com.localagent.privacy.PIIRedactor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MemoryRepository @Inject constructor(
    private val taskRunDao: TaskRunDao,
    private val encryptionManager: EncryptionManager,
    private val piiRedactor: PIIRedactor
) {

    fun getMemories(): Flow<List<TaskRun>> {
        return taskRunDao.getAll().map { list ->
            list.map { run ->
                // Decrypt data on read
                run.copy(
                    task = encryptionManager.decrypt(run.task),
                    steps = encryptionManager.decrypt(run.steps),
                    outcome = encryptionManager.decrypt(run.outcome)
                )
            }
        }
    }

    suspend fun saveTaskRun(task: String, steps: String, outcome: String) {
        val redactedTask = piiRedactor.redact(task)
        val redactedSteps = piiRedactor.redact(steps)
        val redactedOutcome = piiRedactor.redact(outcome)

        val encryptedTask = encryptionManager.encrypt(redactedTask)
        val encryptedSteps = encryptionManager.encrypt(redactedSteps)
        val encryptedOutcome = encryptionManager.encrypt(redactedOutcome)

        val run = TaskRun(
            task = encryptedTask,
            steps = encryptedSteps,
            outcome = encryptedOutcome,
            timestamp = System.currentTimeMillis()
        )
        taskRunDao.insert(run)
    }

    // Simple keyword search
    suspend fun searchSimilarTasks(query: String): List<TaskRun> {
        val keywords = query.split(" ").filter { it.length > 3 }
        // In-memory filter for now (fetch recent/all, then filter)
        // A better approach would be FTS or Vector search
        val allRuns = mutableListOf<TaskRun>()
        val list = taskRunDao.getAll().first()

        list.forEach { run ->
            try {
                val decryptedTask = encryptionManager.decrypt(run.task)
                if (keywords.any { keyword -> decryptedTask.contains(keyword, ignoreCase = true) }) {
                    allRuns.add(run.copy(task = decryptedTask)) // Only decrypt task for matching
                }
            } catch (e: Exception) {
                // Decryption failed?
            }
        }
        return allRuns.take(3) // Return top 3 matches
    }
}

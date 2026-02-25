package com.localagent.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.localagent.data.dao.TaskRunDao
import com.localagent.memory.TaskRun

@Database(entities = [TaskRun::class], version = 1)
abstract class AgentDatabase : RoomDatabase() {
    abstract fun taskRunDao(): TaskRunDao
}

package com.localagent.di

import android.content.Context
import androidx.room.Room
import com.localagent.data.dao.TaskRunDao
import com.localagent.data.db.AgentDatabase
import com.localagent.llm.LLMClient
import com.localagent.llm.OllamaClient
import com.localagent.privacy.EncryptionManager
import com.localagent.privacy.PIIRedactor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAgentDatabase(@ApplicationContext context: Context): AgentDatabase {
        return Room.databaseBuilder(
            context,
            AgentDatabase::class.java,
            "agent_database"
        ).build()
    }

    @Provides
    fun provideTaskRunDao(database: AgentDatabase): TaskRunDao {
        return database.taskRunDao()
    }

    @Provides
    @Singleton
    fun provideLLMClient(client: OllamaClient): LLMClient {
        return client
    }

    @Provides
    @Singleton
    fun provideEncryptionManager(): EncryptionManager {
        return EncryptionManager()
    }

    @Provides
    @Singleton
    fun providePIIRedactor(): PIIRedactor {
        return PIIRedactor()
    }
}

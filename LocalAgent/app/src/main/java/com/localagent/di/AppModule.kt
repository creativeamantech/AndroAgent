package com.localagent.di

import android.content.Context
import androidx.room.Room
import com.localagent.data.dao.TaskRunDao
import com.localagent.data.db.AgentDatabase
import com.localagent.llm.LLMClient
import com.localagent.llm.OllamaClient
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    abstract fun bindLLMClient(
        ollamaClient: OllamaClient
    ): LLMClient

    companion object {
        @Provides
        @Singleton
        fun provideAgentDatabase(@ApplicationContext context: Context): AgentDatabase {
            // In a real app, securely retrieve the passphrase from KeyStore
            val passphrase = SQLiteDatabase.getBytes("localagent-secret-key".toCharArray())
            val factory = SupportFactory(passphrase)

            return Room.databaseBuilder(
                context,
                AgentDatabase::class.java,
                "agent_database"
            )
            .openHelperFactory(factory)
            .build()
        }

        @Provides
        fun provideTaskRunDao(database: AgentDatabase): TaskRunDao {
            return database.taskRunDao()
        }
    }
}

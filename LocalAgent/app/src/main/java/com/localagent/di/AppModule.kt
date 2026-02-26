package com.localagent.di

import android.content.Context
import androidx.room.Room
import com.localagent.data.dao.TaskRunDao
import com.localagent.data.db.AgentDatabase
import com.localagent.llm.GeminiNanoClient
import com.localagent.llm.LLMClient
import com.localagent.llm.OllamaClient
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
object AppModule {

    @Provides
    @Singleton
    fun provideGeminiNanoClient(
        @ApplicationContext context: Context
    ): GeminiNanoClient {
        return GeminiNanoClient(context)
    }

    @Provides
    @Singleton
    fun provideOllamaClient(): OllamaClient {
        return OllamaClient()
    }

    @Provides
    @Singleton
    fun provideDefaultLLMClient(ollamaClient: OllamaClient): LLMClient {
        return ollamaClient
    }

    @Provides
    @Singleton
    fun provideAgentDatabase(@ApplicationContext context: Context): AgentDatabase {
        System.loadLibrary("sqlcipher")

        // Use EncryptedSharedPreferences to store a random key
        // Note: For full security, use MasterKey from androidx.security.crypto
        val masterKeyAlias = androidx.security.crypto.MasterKeys.getOrCreate(androidx.security.crypto.MasterKeys.AES256_GCM_SPEC)
        val sharedPreferences = androidx.security.crypto.EncryptedSharedPreferences.create(
            "secret_shared_prefs",
            masterKeyAlias,
            context,
            androidx.security.crypto.EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            androidx.security.crypto.EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        var key = sharedPreferences.getString("db_key", null)
        if (key == null) {
            val bytes = ByteArray(32)
            java.security.SecureRandom().nextBytes(bytes)
            key = android.util.Base64.encodeToString(bytes, android.util.Base64.DEFAULT)
            sharedPreferences.edit().putString("db_key", key).apply()
        }

        val passphrase = SQLiteDatabase.getBytes(key!!.toCharArray())
        val factory = SupportFactory(passphrase)

        return Room.databaseBuilder(
            context,
            AgentDatabase::class.java,
            "agent_db"
        )
        .openHelperFactory(factory)
        .build()
    }

    @Provides
    @Singleton
    fun provideTaskRunDao(database: AgentDatabase): TaskRunDao {
        return database.taskRunDao()
    }
}

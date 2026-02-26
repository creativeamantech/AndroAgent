package com.localagent.di

import com.localagent.llm.LLMClient
import com.localagent.llm.OllamaClient
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    abstract fun bindLLMClient(
        ollamaClient: OllamaClient
    ): LLMClient
}

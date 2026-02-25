package com.localagent.memory

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MemoryRepository @Inject constructor() {
    private val memory = mutableListOf<String>()

    fun addMemory(memory: String) {
        this.memory.add(memory)
    }

    fun getMemories(): List<String> {
        return memory
    }
}

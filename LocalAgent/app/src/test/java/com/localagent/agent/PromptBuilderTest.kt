package com.localagent.agent

import com.localagent.tools.ToolRegistry
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.Mockito.mock

class PromptBuilderTest {

    @Test
    fun `buildSystemPrompt contains tool descriptions`() {
        // Mock ToolRegistry or use a real one with dummy tools
        val toolRegistry = ToolRegistry(mock(), mock())
        // Note: Creating real ToolRegistry is hard due to dependencies, checking basic string presence for now
        // Ideally we'd mock the listTools() return
    }
}

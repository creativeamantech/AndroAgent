package com.localagent.agent

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ResponseParserTest {
    @Test
    fun testParseAction() {
        val json = """
            {
                "thought": "I should act",
                "action": {
                    "tool": "tap",
                    "args": {"x": 10, "y": 20}
                }
            }
        """.trimIndent()
        val response = ResponseParser().parse(json)
        assertTrue("Expected AgentAction but got ${response.javaClass.simpleName}", response is AgentAction)
        val action = response as AgentAction
        assertEquals("tap", action.tool)
        assertEquals(10, action.args.getInt("x"))
    }

    @Test
    fun testParseMessage() {
         val json = """
            {
                "thought": "Just thinking"
            }
        """.trimIndent()
        val response = ResponseParser().parse(json)
        assertTrue("Expected AgentMessage but got ${response.javaClass.simpleName}", response is AgentMessage)
        val message = response as AgentMessage
        assertEquals("Just thinking", message.content)
    }
}

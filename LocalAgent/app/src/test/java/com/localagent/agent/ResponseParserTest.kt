package com.localagent.agent

import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ResponseParserTest {

    private val parser = ResponseParser()

    @Test
    fun `parse valid JSON`() {
        val json = """
            {
                "thought": "I should tap button",
                "action": {
                    "tool": "tap",
                    "args": { "x": 100, "y": 200 }
                }
            }
        """.trimIndent()

        val result = parser.parse(json)
        assertEquals("I should tap button", result?.thought)
        assertEquals("tap", result?.tool)
        assertEquals(100, result?.args?.getInt("x"))
    }

    @Test
    fun `parse JSON with markdown`() {
        val json = """
            Here is the action:
            ```json
            {
                "action": {
                    "tool": "done",
                    "args": { "result": "success" }
                }
            }
            ```
        """.trimIndent()

        val result = parser.parse(json)
        assertEquals("done", result?.tool)
    }
}

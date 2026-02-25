package com.localagent.privacy

import org.junit.Assert.assertEquals
import org.junit.Test

class PIIRedactorTest {
    @Test
    fun testRedactEmail() {
        val input = "Contact me at test@example.com"
        val expected = "Contact me at [REDACTED_EMAIL]"
        val output = PIIRedactor().redact(input)
        assertEquals(expected, output)
    }

    @Test
    fun testRedactPhone() {
        val input = "Call me at 1234567890"
        val expected = "Call me at [REDACTED_PHONE]"
        val output = PIIRedactor().redact(input)
        assertEquals(expected, output)
    }
}

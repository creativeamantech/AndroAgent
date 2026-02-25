package com.localagent.agent

data class AgentStep(
    val thought: String,
    val action: String?,
    val observation: String?
)

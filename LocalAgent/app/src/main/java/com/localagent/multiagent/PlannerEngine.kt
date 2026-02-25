package com.localagent.multiagent

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlannerEngine @Inject constructor() {
    fun plan(goal: String): List<String> {
        // Placeholder implementation
        return listOf("Step 1: Analyze goal", "Step 2: Execute action", "Step 3: Verify result")
    }
}

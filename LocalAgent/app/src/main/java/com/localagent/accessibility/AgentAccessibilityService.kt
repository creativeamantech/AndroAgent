package com.localagent.accessibility

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AgentAccessibilityService : AccessibilityService() {

    @Inject
    lateinit var accessibilityController: AccessibilityController

    override fun onServiceConnected() {
        super.onServiceConnected()
        accessibilityController.setService(this)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Handle events
    }

    override fun onInterrupt() {
        // Handle interruption
    }

    override fun onUnbind(intent: android.content.Intent?): Boolean {
        accessibilityController.setService(null)
        return super.onUnbind(intent)
    }
}

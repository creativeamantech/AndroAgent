package com.localagent.accessibility

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccessibilityController @Inject constructor() {
    private var service: AccessibilityService? = null

    fun setService(service: AccessibilityService?) {
        this.service = service
    }

    fun getScreenContent(): String {
        val root = service?.rootInActiveWindow
        return if (root != null) {
            try {
                val json = ScreenSerializer.serialize(root)
                json.toString()
            } finally {
                root.recycle() // Important: Recycle root node
            }
        } else {
            "No active window found or Accessibility Service not connected."
        }
    }

    fun tap(x: Float, y: Float) {
        service?.let {
            val path = Path().apply { moveTo(x, y) }
            val gesture = GestureDescription.Builder()
                .addStroke(GestureDescription.StrokeDescription(path, 0, 100))
                .build()
            it.dispatchGesture(gesture, null, null)
        }
    }

    fun swipe(x1: Float, y1: Float, x2: Float, y2: Float) {
        service?.let {
            val path = Path().apply {
                moveTo(x1, y1)
                lineTo(x2, y2)
            }
            val gesture = GestureDescription.Builder()
                .addStroke(GestureDescription.StrokeDescription(path, 0, 300))
                .build()
            it.dispatchGesture(gesture, null, null)
        }
    }

    fun performGlobalAction(action: Int): Boolean {
        return service?.performGlobalAction(action) ?: false
    }
}

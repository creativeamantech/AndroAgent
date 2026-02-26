package com.localagent.accessibility

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.graphics.Rect
import android.os.Bundle
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import org.json.JSONArray
import org.json.JSONObject

class AgentAccessibilityService : AccessibilityService() {

    companion object {
        var instance: AgentAccessibilityService? = null
            private set
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Handle events if needed, for now we just need the service running
    }

    override fun onInterrupt() {
        instance = null
    }

    override fun onDestroy() {
        super.onDestroy()
        instance = null
    }

    fun getScreenContent(): String {
        val root = rootInActiveWindow ?: return "{ \"nodes\": [] }"
        val nodes = JSONArray()
        traverseNode(root, nodes)
        val result = JSONObject()
        result.put("nodes", nodes)
        return result.toString()
    }

    private fun traverseNode(node: AccessibilityNodeInfo, nodes: JSONArray) {
        if (!node.isVisibleToUser) return

        val nodeJson = JSONObject()
        val rect = Rect()
        node.getBoundsInScreen(rect)

        nodeJson.put("class", node.className)
        nodeJson.put("text", node.text ?: "")
        nodeJson.put("contentDescription", node.contentDescription ?: "")
        nodeJson.put("viewId", node.viewIdResourceName ?: "")
        nodeJson.put("clickable", node.isClickable)
        nodeJson.put("editable", node.isEditable)
        nodeJson.put("scrollable", node.isScrollable)
        nodeJson.put("bounds", JSONArray().apply {
            put(rect.left)
            put(rect.top)
            put(rect.width())
            put(rect.height())
        })

        // Only add if it has some content or is interactive
        if (node.isClickable || node.isEditable || !node.text.isNullOrEmpty() || !node.contentDescription.isNullOrEmpty()) {
            nodes.put(nodeJson)
        }

        for (i in 0 until node.childCount) {
            val child = node.getChild(i)
            if (child != null) {
                traverseNode(child, nodes)
                child.recycle()
            }
        }
    }

    fun tap(x: Int, y: Int): Boolean {
        val path = Path()
        path.moveTo(x.toFloat(), y.toFloat())
        val builder = GestureDescription.Builder()
        val gestureDescription = builder
            .addStroke(GestureDescription.StrokeDescription(path, 0, 100))
            .build()
        return dispatchGesture(gestureDescription, null, null)
    }

    fun swipe(x1: Int, y1: Int, x2: Int, y2: Int, duration: Long = 500): Boolean {
        val path = Path()
        path.moveTo(x1.toFloat(), y1.toFloat())
        path.lineTo(x2.toFloat(), y2.toFloat())
        val builder = GestureDescription.Builder()
        val gestureDescription = builder
            .addStroke(GestureDescription.StrokeDescription(path, 0, duration))
            .build()
        return dispatchGesture(gestureDescription, null, null)
    }

    fun back(): Boolean {
        return performGlobalAction(GLOBAL_ACTION_BACK)
    }

    fun home(): Boolean {
        return performGlobalAction(GLOBAL_ACTION_HOME)
    }

    fun recents(): Boolean {
        return performGlobalAction(GLOBAL_ACTION_RECENTS)
    }

    fun notifications(): Boolean {
        return performGlobalAction(GLOBAL_ACTION_NOTIFICATIONS)
    }

    fun typeText(text: String): Boolean {
        val focusedNode = rootInActiveWindow?.findFocus(AccessibilityNodeInfo.FOCUS_INPUT)
        if (focusedNode != null && focusedNode.isEditable) {
            val arguments = Bundle()
            arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text)
            return focusedNode.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)
        }
        return false
    }
}

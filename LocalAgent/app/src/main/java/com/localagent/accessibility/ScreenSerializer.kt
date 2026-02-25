package com.localagent.accessibility

import android.graphics.Rect
import android.view.accessibility.AccessibilityNodeInfo
import org.json.JSONArray
import org.json.JSONObject

object ScreenSerializer {

    fun serialize(root: AccessibilityNodeInfo): JSONObject {
        return serializeNode(root)
    }

    private fun serializeNode(node: AccessibilityNodeInfo): JSONObject {
        val json = JSONObject()
        val rect = Rect()
        node.getBoundsInScreen(rect)

        try {
            json.put("class", node.className ?: "")
            json.put("package", node.packageName ?: "")
            json.put("id", node.viewIdResourceName ?: "")
            json.put("text", node.text ?: "")
            json.put("desc", node.contentDescription ?: "")
            json.put("clickable", node.isClickable)
            json.put("enabled", node.isEnabled)
            json.put("scrollable", node.isScrollable)
            json.put("bounds", "[ ${rect.left}, ${rect.top}, ${rect.right}, ${rect.bottom} ]")

            if (node.childCount > 0) {
                val children = JSONArray()
                for (i in 0 until node.childCount) {
                    val child = node.getChild(i)
                    if (child != null) {
                        children.put(serializeNode(child))
                        child.recycle() // Important: Recycle child node
                    }
                }
                json.put("children", children)
            }
        } catch (e: Exception) {
            json.put("error", "Serialization failed: ${e.message}")
        }
        return json
    }
}

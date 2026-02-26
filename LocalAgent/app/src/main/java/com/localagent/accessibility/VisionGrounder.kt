package com.localagent.accessibility

import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VisionGrounder @Inject constructor() {

    data class UIElement(
        val text: String,
        val type: String,
        val bounds: Bounds
    )

    data class Bounds(
        val x: Int,
        val y: Int,
        val w: Int,
        val h: Int
    ) {
        fun centerX(): Int = x + w / 2
        fun centerY(): Int = y + h / 2
    }

    fun parseVLMResponse(jsonString: String): List<UIElement> {
        val elements = mutableListOf<UIElement>()
        try {
            val json = JSONObject(jsonString)
            val jsonElements = json.optJSONArray("elements") ?: JSONArray()

            for (i in 0 until jsonElements.length()) {
                val element = jsonElements.getJSONObject(i)
                val text = element.optString("text")
                val type = element.optString("type")
                val boundsArray = element.optJSONArray("bounds")

                if (boundsArray != null && boundsArray.length() == 4) {
                    val bounds = Bounds(
                        boundsArray.getInt(0),
                        boundsArray.getInt(1),
                        boundsArray.getInt(2),
                        boundsArray.getInt(3)
                    )
                    elements.add(UIElement(text, type, bounds))
                }
            }
        } catch (e: Exception) {
            // Log parsing error
        }
        return elements
    }

    fun findElement(elements: List<UIElement>, query: String): UIElement? {
        // Simple keyword match or fuzzy search
        return elements.find { it.text.contains(query, ignoreCase = true) }
    }
}

package com.localagent.tools

import android.content.Context
import android.content.Intent
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONObject
import javax.inject.Inject

class OpenAppTool @Inject constructor(@ApplicationContext private val context: Context) : AgentTool {
    override val name: String = "open_app"
    override val description: String = "Open an app by package name"
    override val argsSchema: JSONObject = JSONObject("""
        {
            "type": "object",
            "properties": {
                "package": { "type": "string" }
            },
            "required": ["package"]
        }
    """)

    override suspend fun execute(args: JSONObject): ToolResult {
        val packageName = args.optString("package")
        val intent = context.packageManager.getLaunchIntentForPackage(packageName)
        return if (intent != null) {
            context.startActivity(intent)
            ToolResult("Opened app: $packageName")
        } else {
            ToolResult("App not found: $packageName")
        }
    }
}

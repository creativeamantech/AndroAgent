package com.localagent.plugins.builtin

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.localagent.tools.AgentTool
import com.localagent.tools.ToolResult
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpotifyTools @Inject constructor(
    @ApplicationContext private val context: Context
) {

    val playMusicTool = object : AgentTool {
        override val name = "spotify_play"
        override val description = "Play music on Spotify using a search query."
        override val argsSchema = JSONObject("""{
            "type": "object",
            "properties": {
                "query": {"type": "string"}
            },
            "required": ["query"]
        }""")

        override suspend fun execute(args: JSONObject): ToolResult {
            val query = args.optString("query")
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("spotify:search:$query")
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

            // This is a simplification. Real Spotify integration might use Android Media Intents or Spotify SDK.
            // Or simpler: specific intents.
            // For "play", maybe use MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH?

            try {
                context.startActivity(intent)
                return ToolResult("Launched Spotify for query: $query")
            } catch (e: Exception) {
                return ToolResult("Failed to open Spotify: ${e.message}")
            }
        }
    }

    fun getTools(): List<AgentTool> = listOf(playMusicTool)
}

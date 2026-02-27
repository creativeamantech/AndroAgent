package com.localagent.plugins.builtin

import com.localagent.tools.AgentTool
import com.localagent.tools.ToolResult
import org.json.JSONObject
import javax.inject.Inject

class SpotifyTools @Inject constructor() {
    val playMusicTool = object : AgentTool {
        override val name = "spotify_play"
        override val description = "Play music on Spotify"
        override val argsSchema = JSONObject("""{"type": "object", "properties": {"query": {"type": "string"}}, "required": ["query"]}""")

        override suspend fun execute(args: JSONObject): ToolResult {
            val query = args.optString("query")
            // In a real app, send Intent to Spotify
            return ToolResult("Playing $query on Spotify (Stub)")
        }
    }
}

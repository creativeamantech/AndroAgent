package com.localagent.plugins

import com.localagent.plugins.builtin.SpotifyTools
import com.localagent.tools.AgentTool
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PluginRegistry @Inject constructor(
    private val spotifyTools: SpotifyTools
) {
    fun getBuiltinTools(): List<AgentTool> {
        return listOf(
            spotifyTools.playMusicTool
        )
    }

    // Future: Load external plugins via PackageManager
}

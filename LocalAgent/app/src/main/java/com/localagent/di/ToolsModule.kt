package com.localagent.di

import com.localagent.tools.AgentTool
import com.localagent.tools.OpenAppTool
import com.localagent.tools.ReadScreenTool
import com.localagent.tools.TapTool
import com.localagent.tools.SwipeTool
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
abstract class ToolsModule {

    @Binds
    @IntoSet
    abstract fun bindTapTool(tool: TapTool): AgentTool

    @Binds
    @IntoSet
    abstract fun bindSwipeTool(tool: SwipeTool): AgentTool

    @Binds
    @IntoSet
    abstract fun bindOpenAppTool(tool: OpenAppTool): AgentTool

    @Binds
    @IntoSet
    abstract fun bindReadScreenTool(tool: ReadScreenTool): AgentTool
}

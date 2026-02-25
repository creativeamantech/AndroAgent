package com.localagent.ui.plugins

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localagent.plugins.PluginRegistry
import com.localagent.tools.AgentTool
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PluginStoreViewModel @Inject constructor(
    private val pluginRegistry: PluginRegistry
) : ViewModel() {

    private val _plugins = MutableStateFlow<List<AgentTool>>(emptyList())
    val plugins = _plugins.asStateFlow()

    init {
        loadPlugins()
    }

    fun loadPlugins() {
        viewModelScope.launch {
            _plugins.value = pluginRegistry.getPlugins()
        }
    }
}

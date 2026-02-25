package com.localagent.ui.plugins

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.localagent.tools.AgentTool

@Composable
fun PluginStoreScreen(
    onBackClick: () -> Unit,
    viewModel: PluginStoreViewModel = hiltViewModel()
) {
    val plugins by viewModel.plugins.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Plugin Store", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(plugins) { plugin ->
                PluginItem(plugin)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = onBackClick) {
            Text("Back")
        }
    }
}

@Composable
fun PluginItem(plugin: AgentTool) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(plugin.name, style = MaterialTheme.typography.titleMedium)
                Text(plugin.description, style = MaterialTheme.typography.bodySmall)
            }
            Switch(checked = true, onCheckedChange = { /* TODO: Implement Enable/Disable */ })
        }
    }
}

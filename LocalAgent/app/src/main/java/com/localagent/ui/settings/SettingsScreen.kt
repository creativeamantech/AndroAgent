package com.localagent.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    onManageModelsClick: () -> Unit,
    onPrivacySettingsClick: () -> Unit,
    onPluginStoreClick: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val ollamaUrl by viewModel.ollamaUrl.collectAsState()
    val isMultiAgentMode by viewModel.isMultiAgentMode.collectAsState()
    var urlInput by remember { mutableStateOf(ollamaUrl) }

    LaunchedEffect(ollamaUrl) {
        urlInput = ollamaUrl
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Settings", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = urlInput,
            onValueChange = { urlInput = it },
            label = { Text("Ollama Base URL") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = { viewModel.updateOllamaUrl(urlInput) },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Save URL")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Multi-Agent Mode")
            Switch(
                checked = isMultiAgentMode,
                onCheckedChange = { viewModel.toggleMultiAgentMode(it) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onManageModelsClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Manage Models")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onPluginStoreClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Plugin Store")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onPrivacySettingsClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Privacy Settings")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = onBackClick) {
            Text("Back")
        }
    }
}

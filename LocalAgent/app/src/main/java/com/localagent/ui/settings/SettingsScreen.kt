package com.localagent.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val ollamaUrl by viewModel.ollamaUrl.collectAsState()
    var urlInput by remember { mutableStateOf(ollamaUrl) }

    // Sync input with flow if flow updates externally (optional but good practice)
    LaunchedEffect(ollamaUrl) {
        urlInput = ollamaUrl
    }

    Column(modifier = Modifier.padding(16.dp)) {
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
            Text("Save")
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(onClick = onBackClick) {
            Text("Back")
        }
    }
}

package com.localagent.ui.models

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.localagent.llm.ModelInfo

@Composable
fun ModelManagerScreen(
    onBackClick: () -> Unit,
    viewModel: ModelManagerViewModel = hiltViewModel()
) {
    val models by viewModel.models.collectAsState()
    val pullStatus by viewModel.pullStatus.collectAsState()
    val isPulling by viewModel.isPulling.collectAsState()
    var newModelName by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Model Manager", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        // Pull new model
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = newModelName,
                onValueChange = { newModelName = it },
                label = { Text("Model Name (e.g. llama3)") },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = { viewModel.pullModel(newModelName) },
                enabled = !isPulling && newModelName.isNotBlank()
            ) {
                Text(if (isPulling) "Pulling..." else "Pull")
            }
        }

        if (isPulling) {
            Text(pullStatus, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 8.dp))
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // List models
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(models) { model ->
                ModelItem(model, onDelete = { viewModel.deleteModel(model.name) })
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = onBackClick) {
            Text("Back")
        }
    }
}

@Composable
fun ModelItem(model: ModelInfo, onDelete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(model.name, style = MaterialTheme.typography.titleMedium)
                Text("${model.size / 1024 / 1024} MB", style = MaterialTheme.typography.bodySmall)
            }
            IconButton(onClick = onDelete) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }
}

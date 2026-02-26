package com.localagent.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.localagent.multiagent.OrchestratorState
import com.localagent.multiagent.Subtask
import com.localagent.multiagent.SubtaskStatus

@Composable
fun ChatScreen(
    onSettingsClick: () -> Unit,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val messages by viewModel.messages.collectAsState()
    val orchestratorState by viewModel.orchestratorState.collectAsState()
    var inputText by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(title = { Text("LocalAgent") }, actions = {
            IconButton(onClick = onSettingsClick) {
                // Icon(Icons.Default.Settings, contentDescription = "Settings")
                Text("Settings")
            }
        })

        LazyColumn(modifier = Modifier.weight(1f).padding(16.dp)) {
            items(messages) { message ->
                MessageBubble(message)
            }
            if (orchestratorState.isPlanning || orchestratorState.currentActivity.isNotEmpty()) {
                item {
                    StatusPanel(orchestratorState)
                }
            }
        }

        Row(modifier = Modifier.padding(16.dp)) {
            TextField(
                value = inputText,
                onValueChange = { inputText = it },
                modifier = Modifier.weight(1f)
            )
            Button(onClick = {
                if (inputText.isNotBlank()) {
                    viewModel.sendMessage(inputText)
                    inputText = ""
                }
            }) {
                Text("Send")
            }
        }
    }
}

@Composable
fun MessageBubble(message: ChatMessage) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start) {
        Card(modifier = Modifier.padding(8.dp), colors = CardDefaults.cardColors(containerColor = if (message.isUser) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant)) {
            Text(text = message.text, modifier = Modifier.padding(8.dp))
        }
    }
}

@Composable
fun StatusPanel(state: OrchestratorState) {
    Card(modifier = Modifier.fillMaxWidth().padding(8.dp), colors = CardDefaults.cardColors(containerColor = Color.LightGray)) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text("Activity: ${state.currentActivity}", style = MaterialTheme.typography.labelLarge)

            if (state.subtasks.isNotEmpty()) {
                Divider(modifier = Modifier.padding(vertical = 4.dp))
                Text("Plan:", style = MaterialTheme.typography.titleSmall)
                state.subtasks.forEach { subtask ->
                    SubtaskRow(subtask)
                }
            }
        }
    }
}

@Composable
fun SubtaskRow(subtask: Subtask) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text("- ${subtask.description}", modifier = Modifier.weight(1f))
        val color = when (subtask.status) {
            SubtaskStatus.PENDING -> Color.Gray
            SubtaskStatus.RUNNING -> Color.Blue
            SubtaskStatus.COMPLETED -> Color.Green
            SubtaskStatus.FAILED -> Color.Red
        }
        Text(subtask.status.name, color = color, style = MaterialTheme.typography.labelSmall)
    }
}

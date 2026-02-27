package com.localagent.ui.chat

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.localagent.agent.AgentState
import com.localagent.llm.Message

@Composable
fun ChatScreen(
    onSettingsClick: () -> Unit,
    viewModel: ChatViewModel = hiltViewModel()
) {
    var text by remember { mutableStateOf("") }
    val messages by viewModel.messages.collectAsState()
    val state by viewModel.state.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Header
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text("LocalAgent", style = MaterialTheme.typography.headlineMedium)
            IconButton(onClick = onSettingsClick) {
                Icon(imageVector = Icons.Filled.Settings, contentDescription = "Settings")
            }
        }

        // Status Indicator
        if (state !is AgentState.Idle && state !is AgentState.Done && state !is AgentState.Error) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            Text(
                text = when (state) {
                    is AgentState.Thinking -> "Thinking..."
                    is AgentState.Acting -> "Acting: ${(state as AgentState.Acting).tool}"
                    else -> "Processing..."
                },
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        // Chat History
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(messages) { message ->
                MessageBubble(message)
            }
        }

        // Input
        Row(modifier = Modifier.fillMaxWidth()) {
            TextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Ask me anything...") }
            )
            Button(onClick = {
                viewModel.sendMessage(text)
                text = ""
            }, enabled = state is AgentState.Idle || state is AgentState.Done || state is AgentState.Error) {
                Text("Send")
            }
        }
    }
}

@Composable
fun MessageBubble(message: Message) {
    Card(modifier = Modifier.padding(8.dp)) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(text = message.role, style = MaterialTheme.typography.labelSmall)
            Text(text = message.content)
        }
    }
}

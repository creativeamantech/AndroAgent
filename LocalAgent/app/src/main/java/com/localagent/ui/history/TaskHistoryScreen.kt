package com.localagent.ui.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.localagent.memory.TaskRun
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TaskHistoryScreen(
    onBackClick: () -> Unit,
    viewModel: TaskHistoryViewModel = hiltViewModel()
) {
    val taskRuns by viewModel.taskRuns.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Task History", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(taskRuns) { run ->
                TaskRunItem(run)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = onBackClick) {
            Text("Back")
        }
    }
}

@Composable
fun TaskRunItem(run: TaskRun) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    val dateStr = dateFormat.format(Date(run.timestamp))

    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = run.task, style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(text = run.outcome, style = MaterialTheme.typography.labelMedium, color = if (run.outcome == "SUCCESS") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
                Text(text = dateStr, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

package com.localagent.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun PrivacySettingsScreen(
    onBackClick: () -> Unit,
    viewModel: PrivacySettingsViewModel = hiltViewModel()
) {
    val logSmsCall by viewModel.logSmsCall.collectAsState()
    val autoDeleteInterval by viewModel.autoDeleteInterval.collectAsState()
    val isStealthMode by viewModel.isStealthMode.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Privacy Settings", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        SwitchItem(
            title = "Log SMS/Calls",
            description = "Allow logging of SMS and Call task history",
            checked = logSmsCall,
            onCheckedChange = { viewModel.toggleLogSmsCall(it) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Auto-delete logs after (days)", style = MaterialTheme.typography.titleMedium)
        Slider(
            value = autoDeleteInterval.toFloat(),
            onValueChange = { viewModel.updateAutoDeleteInterval(it.toInt()) },
            valueRange = 7f..90f,
            steps = 82
        )
        Text("$autoDeleteInterval days")

        Spacer(modifier = Modifier.height(16.dp))

        SwitchItem(
            title = "Stealth Mode",
            description = "Disable ALL logging for this session",
            checked = isStealthMode,
            onCheckedChange = { viewModel.toggleStealthMode(it) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = { /* TODO: Implement Export */ }, modifier = Modifier.fillMaxWidth()) {
            Text("Export Encrypted Backup")
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(onClick = onBackClick) {
            Text("Back")
        }
    }
}

@Composable
fun SwitchItem(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(description, style = MaterialTheme.typography.bodySmall)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

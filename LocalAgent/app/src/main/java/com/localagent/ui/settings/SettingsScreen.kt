package com.localagent.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun SettingsScreen(onBackClick: () -> Unit) {
    Column {
        Button(onClick = onBackClick) {
            Text("Back")
        }
        Text("Settings Placeholder")
    }
}

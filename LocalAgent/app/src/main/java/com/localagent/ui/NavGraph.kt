package com.localagent.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.localagent.ui.chat.ChatScreen
import com.localagent.ui.history.TaskHistoryScreen
import com.localagent.ui.models.ModelManagerScreen
import com.localagent.ui.settings.SettingsScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "chat") {
        composable("chat") {
            ChatScreen(
                onSettingsClick = { navController.navigate("settings") },
                onHistoryClick = { navController.navigate("history") }
            )
        }
        composable("settings") {
            SettingsScreen(
                onBackClick = { navController.popBackStack() },
                onManageModelsClick = { navController.navigate("models") }
            )
        }
        composable("history") {
            TaskHistoryScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        composable("models") {
            ModelManagerScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}

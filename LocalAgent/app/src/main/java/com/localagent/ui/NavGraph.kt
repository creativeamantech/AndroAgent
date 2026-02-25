package com.localagent.ui

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.localagent.ui.chat.ChatScreen
import com.localagent.ui.history.TaskHistoryScreen
import com.localagent.ui.models.ModelManagerScreen
import com.localagent.ui.onboarding.OnboardingScreen
import com.localagent.ui.plugins.PluginStoreScreen
import com.localagent.ui.settings.PrivacySettingsScreen
import com.localagent.ui.settings.SettingsScreen
import com.localagent.ui.settings.SettingsViewModel

@Composable
fun NavGraph(startDestination: String = "chat") {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = startDestination) {
        composable("onboarding") {
            val viewModel: SettingsViewModel = hiltViewModel()
            OnboardingScreen(
                onFinish = {
                    // Update first launch flag
                    // We need to access repository or viewmodel to set isFirstLaunch = false
                    // Ideally we'd use a ViewModel for onboarding or pass a lambda that calls one
                    // For simplicity, let's assume OnboardingScreen calls a ViewModel function if we inject it,
                    // or we handle it here via a side effect if we had the repository exposed.
                    // But typically we don't expose repository directly to NavGraph.
                    // Let's use SettingsViewModel here as a hack or create OnboardingViewModel.
                    // Using SettingsViewModel for now since it has the repo.
                    viewModel.updateFirstLaunch(false)
                    navController.navigate("chat") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                }
            )
        }
        composable("chat") {
            ChatScreen(
                onSettingsClick = { navController.navigate("settings") },
                onHistoryClick = { navController.navigate("history") }
            )
        }
        composable("settings") {
            SettingsScreen(
                onBackClick = { navController.popBackStack() },
                onManageModelsClick = { navController.navigate("models") },
                onPrivacySettingsClick = { navController.navigate("privacy") },
                onPluginStoreClick = { navController.navigate("plugins") }
            )
        }
        composable("privacy") {
            PrivacySettingsScreen(
                onBackClick = { navController.popBackStack() }
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
        composable("plugins") {
            PluginStoreScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}

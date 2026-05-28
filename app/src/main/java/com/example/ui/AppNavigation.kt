package com.example.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.LexiconViewModel
import com.example.ui.screens.*

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Items : Screen("items", "Items", Icons.Default.List)
    object Calendar : Screen("calendar", "Calendar", Icons.Default.CalendarMonth)
    object Revision : Screen("revision", "Revision", Icons.Default.Style)
    object Quiz : Screen("quiz", "Quiz", Icons.Default.Quiz)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
}

val items = listOf(
    Screen.Items,
    Screen.Calendar,
    Screen.Revision,
    Screen.Quiz,
    Screen.Settings
)

@Composable
fun LexiconApp(viewModel: LexiconViewModel = viewModel()) {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(navController, startDestination = Screen.Items.route, Modifier.padding(innerPadding)) {
            composable(Screen.Items.route) { ItemsScreen(viewModel, navController) }
            composable(Screen.Calendar.route) { CalendarScreen(viewModel) }
            composable(Screen.Revision.route) { RevisionScreen(viewModel) }
            composable(Screen.Quiz.route) { QuizScreen(viewModel) }
            composable(Screen.Settings.route) { SettingsScreen(viewModel) }
        }
    }
}

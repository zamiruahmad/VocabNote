package com.example.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.remember
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Style
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.LexiconViewModel
import com.example.ui.screens.*
import kotlinx.coroutines.launch

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Items : Screen("items", "Items", Icons.AutoMirrored.Outlined.List)
    object Calendar : Screen("calendar", "Calendar", Icons.Outlined.CalendarMonth)
    object Revision : Screen("revision", "Revision", Icons.Outlined.Style)
    object Analytics : Screen("analytics", "Analytics", Icons.Outlined.Analytics)
    object Settings : Screen("settings", "Settings", Icons.Outlined.Settings)
}

val navItems = listOf(
    Screen.Items,
    Screen.Calendar,
    Screen.Revision,
    Screen.Analytics,
    Screen.Settings
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LexiconApp(viewModel: LexiconViewModel = viewModel()) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    
    val allGroups by viewModel.allGroups.collectAsStateWithLifecycle()
    val filterGroupId by viewModel.filterGroupId.collectAsStateWithLifecycle()
    val repositoryItems by viewModel.items.collectAsStateWithLifecycle()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier.width(300.dp)
            ) {
                Spacer(modifier = Modifier.padding(16.dp))
                Row(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(32.dp).background(MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp)))
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            "VocabNote", 
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            fontFamily = com.example.ui.theme.appFontFamily
                        )
                    }
                    IconButton(
                        onClick = { scope.launch { drawerState.close() } },
                        modifier = Modifier.size(32.dp).background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                    ) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = "Close", modifier = Modifier.size(20.dp))
                    }
                }
                
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                
                Text(
                    "Groups",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
                )

                val isItemsScreen = currentDestination?.route == Screen.Items.route

                NavigationDrawerItem(
                    label = { Text("All Groups") },
                    icon = { Icon(if (filterGroupId == null && isItemsScreen) Icons.Default.FolderOpen else Icons.Default.Folder, contentDescription = null) },
                    selected = filterGroupId == null && isItemsScreen,
                    onClick = {
                        viewModel.updateFilterGroup(null)
                        navController.navigate(Screen.Items.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                allGroups.forEach { group ->
                    val groupItemCount = repositoryItems.filter { it.groupId == group.id }.size
                    NavigationDrawerItem(
                        label = { Text(group.name) },
                        icon = { Icon(Icons.Default.Folder, contentDescription = null) },
                        selected = filterGroupId == group.id && isItemsScreen,
                        onClick = {
                            viewModel.updateFilterGroup(group.id)
                            navController.navigate(Screen.Items.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                        badge = { Text(groupItemCount.toString(), style = MaterialTheme.typography.labelSmall) }
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                Text(
                    "Tools",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
                )

                navItems.forEach { screen ->
                    val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                    NavigationDrawerItem(
                        label = { Text(screen.title) },
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        selected = selected,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        }
    ) {
        // App Content using CompositionLocal to allow screens to open the drawer
        CompositionLocalProvider(LocalDrawerState provides drawerState) {
            Scaffold(
                bottomBar = {
                    val navBarColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.65f)
                    val selectedPillColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f)
                    val selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer
                    val defaultIconColor = MaterialTheme.colorScheme.onSurfaceVariant

                    Row(
                        modifier = Modifier
                            .navigationBarsPadding()
                            .padding(horizontal = 24.dp, vertical = 24.dp)
                            .fillMaxWidth()
                            .height(68.dp)
                            .shadow(elevation = 8.dp, shape = RoundedCornerShape(34.dp), spotColor = Color.Black.copy(alpha = 0.05f))
                            .clip(RoundedCornerShape(34.dp))
                            .background(navBarColor)
                            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(34.dp)),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        navItems.forEach { screen ->
                            val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                            val color by androidx.compose.animation.animateColorAsState(
                                targetValue = if (selected) selectedIconColor else defaultIconColor,
                                animationSpec = androidx.compose.animation.core.tween(300),
                                label = "color"
                            )
                            
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        navController.navigate(screen.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                val backgroundAlpha by androidx.compose.animation.core.animateFloatAsState(
                                    targetValue = if (selected) 1f else 0f,
                                    animationSpec = androidx.compose.animation.core.tween(300),
                                    label = "alpha"
                                )
                                Box(
                                    modifier = Modifier
                                        .height(52.dp)
                                        .fillMaxWidth(0.85f)
                                        .clip(RoundedCornerShape(26.dp))
                                        .background(selectedPillColor.copy(alpha = backgroundAlpha)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = screen.icon,
                                        contentDescription = screen.title,
                                        tint = color,
                                        modifier = Modifier.size(26.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            ) { innerPadding ->
                NavHost(navController, startDestination = Screen.Items.route, Modifier.padding(innerPadding)) {
                    composable(Screen.Items.route) { ItemsScreen(viewModel, navController) }
                    composable(Screen.Calendar.route) { CalendarScreen(viewModel) }
                    composable(Screen.Revision.route) { RevisionScreen(viewModel) }
                    composable(Screen.Analytics.route) { AnalyticsScreen(viewModel) }
                    composable(Screen.Settings.route) { SettingsScreen(viewModel) }
                }
            }
        }
    }
}

val LocalDrawerState = staticCompositionLocalOf<DrawerState> {
    error("DrawerState not provided")
}

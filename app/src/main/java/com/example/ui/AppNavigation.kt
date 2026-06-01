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
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.filled.CrisisAlert
import androidx.compose.material.icons.outlined.CrisisAlert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Add
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

sealed class Screen(val route: String, val title: String, val selectedIcon: ImageVector, val unselectedIcon: ImageVector) {
    object Items : Screen("items", "Items", Icons.Filled.Home, Icons.Outlined.Home)
    object Revision : Screen("revision", "Revision", Icons.Filled.CheckCircle, Icons.Outlined.CheckCircle)
    object Calendar : Screen("calendar", "Calendar", Icons.Filled.CalendarMonth, Icons.Outlined.CalendarMonth)
    object Analytics : Screen("analytics", "Analytics", Icons.Filled.CrisisAlert, Icons.Outlined.CrisisAlert)
    object Settings : Screen("settings", "Settings", Icons.Filled.Settings, Icons.Outlined.Settings)
}

val headerNavItems = listOf(
    Screen.Items,
    Screen.Revision,
    Screen.Calendar,
    Screen.Analytics
)
val drawerNavItems = listOf(
    Screen.Items,
    Screen.Revision,
    Screen.Calendar,
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
    val showBottomNav by viewModel.showBottomNav.collectAsStateWithLifecycle()

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
                            fontWeight = FontWeight.Bold
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
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = Color.White.copy(alpha = 0.2f),
                        selectedIconColor = Color.White,
                        selectedTextColor = Color.White,
                        unselectedContainerColor = Color.Transparent,
                        unselectedIconColor = Color.White.copy(alpha = 0.6f),
                        unselectedTextColor = Color.White.copy(alpha = 0.6f)
                    )
                )

                allGroups.forEach { group ->
                    val groupItemCount = repositoryItems.filter { it.groupId == group.id }.size
                    val isSelected = filterGroupId == group.id && isItemsScreen
                    NavigationDrawerItem(
                        label = { Text(group.name) },
                        icon = { Icon(Icons.Default.Folder, contentDescription = null) },
                        selected = isSelected,
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
                        badge = { Text(groupItemCount.toString(), style = MaterialTheme.typography.labelSmall, color = if(isSelected) Color.White else Color.White.copy(alpha = 0.6f)) },
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = Color.White.copy(alpha = 0.2f),
                            selectedIconColor = Color.White,
                            selectedTextColor = Color.White,
                            unselectedContainerColor = Color.Transparent,
                            unselectedIconColor = Color.White.copy(alpha = 0.6f),
                            unselectedTextColor = Color.White.copy(alpha = 0.6f)
                        )
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                Text(
                    "Tools",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
                )

                drawerNavItems.forEach { screen ->
                    val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                    NavigationDrawerItem(
                        label = { Text(screen.title) },
                        icon = { Icon(if (selected) screen.selectedIcon else screen.unselectedIcon, contentDescription = screen.title) },
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
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = Color.White.copy(alpha = 0.2f),
                            selectedIconColor = Color.White,
                            selectedTextColor = Color.White,
                            unselectedContainerColor = Color.Transparent,
                            unselectedIconColor = Color.White.copy(alpha = 0.6f),
                            unselectedTextColor = Color.White.copy(alpha = 0.6f)
                        )
                    )
                }
            }
        }
    ) {
        // App Content using CompositionLocal to allow screens to open the drawer
        CompositionLocalProvider(LocalDrawerState provides drawerState) {
            Scaffold(
                containerColor = Color.Transparent,
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        androidx.compose.ui.graphics.Brush.radialGradient(
                            colors = listOf(Color(0xFF1B4E6B), Color(0xFF0A1823)),
                            radius = 1500f
                        )
                    ),
                bottomBar = {
                    val navBarColor = Color.White.copy(alpha = 0.1f)
                    val glassBorder = Color.White.copy(alpha = 0.2f)
                    val selectedIconColor = Color.White
                    val defaultIconColor = Color.White.copy(alpha = 0.6f)

                    val selectedIndex = headerNavItems.indexOfFirst { screen ->
                        currentDestination?.hierarchy?.any { it.route == screen.route } == true
                    }.takeIf { it >= 0 } ?: 0

                    val indicatorOffsetFraction by androidx.compose.animation.core.animateFloatAsState(
                        targetValue = selectedIndex.toFloat(),
                        animationSpec = androidx.compose.animation.core.spring(
                            dampingRatio = 0.7f,
                            stiffness = 300f
                        ),
                        label = "indicatorOffset"
                    )

                    Row(
                        modifier = Modifier
                            .navigationBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (showBottomNav) {
                            BoxWithConstraints(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(68.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(navBarColor)
                                    .border(1.dp, glassBorder, RoundedCornerShape(50))
                                    .padding(horizontal = 8.dp)
                            ) {
                                val itemWidth = maxWidth / headerNavItems.size
                                
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .width(itemWidth)
                                        .offset(x = itemWidth * indicatorOffsetFraction),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .height(44.dp)
                                            .width(56.dp)
                                            .clip(RoundedCornerShape(50))
                                            .background(Color.White.copy(alpha = 0.2f))
                                    )
                                }
                                
                                Row(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    headerNavItems.forEach { screen ->
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
                                            Icon(
                                                imageVector = if (selected) screen.selectedIcon else screen.unselectedIcon,
                                                contentDescription = screen.title,
                                                tint = color,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                    }
                                }
                            }
                            
                            Spacer(modifier = Modifier.width(16.dp))
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                        
                        Box(
                            modifier = Modifier
                                .size(68.dp)
                                .clip(CircleShape)
                                .background(navBarColor)
                                .border(1.dp, glassBorder, CircleShape)
                                .clickable {
                                    viewModel.setShowAddDialog(true)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add Item", tint = Color.White, modifier = Modifier.size(28.dp))
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

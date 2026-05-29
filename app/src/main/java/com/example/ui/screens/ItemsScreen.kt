package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.LexiconViewModel
import com.example.data.Item
import com.example.data.LexiconGroup
import com.example.ui.LocalDrawerState
import com.example.ui.components.GROUP_ICONS
import com.example.ui.components.ItemModal
import com.example.ui.components.SidebarOutlinedIcon
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemsScreen(viewModel: LexiconViewModel, navController: NavController) {
    val items by viewModel.items.collectAsStateWithLifecycle()
    val allGroups by viewModel.allGroups.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val customCategories by viewModel.customCategories.collectAsStateWithLifecycle()
    val customLanguages by viewModel.customLanguages.collectAsStateWithLifecycle()
    val filterGroupId by viewModel.filterGroupId.collectAsStateWithLifecycle()
    val filterCategory by viewModel.filterCategory.collectAsStateWithLifecycle()

    var showAddDialog by remember { mutableStateOf(false) }
    var editingItem by remember { mutableStateOf<Item?>(null) }
    var isSearching by remember { mutableStateOf(false) }

    val drawerState = LocalDrawerState.current
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            if (isSearching) {
                TopAppBar(
                    title = {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = viewModel::updateSearchQuery,
                            modifier = Modifier.fillMaxWidth().padding(end = 8.dp),
                            placeholder = { Text("Search...") },
                            singleLine = true,
                            trailingIcon = {
                                IconButton(onClick = {
                                    isSearching = false
                                    viewModel.updateSearchQuery("")
                                }) {
                                    Icon(Icons.Default.Close, contentDescription = "Close search")
                                }
                            }
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
                )
            } else {
                TopAppBar(
                    title = { 
                        Text(
                            text = if (filterGroupId == null) "VocabNote (${items.size})" else "${allGroups.find { it.id == filterGroupId }?.name ?: "VocabNote"} (${items.size})",
                            fontWeight = FontWeight.Black,
                            fontFamily = com.example.ui.theme.appFontFamily
                        ) 
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            SidebarOutlinedIcon(contentDescription = "Menu")
                        }
                    },
                    actions = {
                        IconButton(onClick = { /* TODO: Notifications */ }) {
                            Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                        }
                        IconButton(onClick = { isSearching = true }) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Item")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            androidx.compose.foundation.lazy.LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = filterCategory == null,
                        onClick = { viewModel.updateFilterCategory(null) },
                        label = { Text("All") }
                    )
                }
                items(customCategories) { category ->
                    FilterChip(
                        selected = filterCategory == category,
                        onClick = { viewModel.updateFilterCategory(category) },
                        label = { Text(category) }
                    )
                }
            }
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(items, key = { it.id }) { item ->
                    ItemCard(
                        item = item,
                        groups = allGroups,
                        onEdit = {
                            editingItem = item
                            showAddDialog = true
                        },
                        onDelete = { viewModel.deleteItem(item.id) }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        ItemModal(
            initialItem = editingItem,
            groups = allGroups,
            customCategories = customCategories,
            customLanguages = customLanguages,
            onSave = { newItem ->
                if (editingItem != null) {
                    viewModel.updateItem(newItem.copy(id = editingItem!!.id))
                } else {
                    viewModel.insertItem(newItem)
                }
                showAddDialog = false
                editingItem = null
            },
            onDismiss = {
                showAddDialog = false
                editingItem = null
            }
        )
    }
}

@Composable
fun ItemCard(
    item: Item,
    groups: List<LexiconGroup>,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val group = groups.find { it.id == item.groupId }
    val groupColorStr = group?.colorHex ?: "#888888"
    val groupColor = try {
        Color(android.graphics.Color.parseColor(groupColorStr))
    } catch (e: Exception) {
        Color.Gray
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Badge(containerColor = MaterialTheme.colorScheme.primary) { Text(item.category) }
                    if (group != null) {
                        val safeIconIndex = group.iconIndex.coerceIn(0, GROUP_ICONS.lastIndex)
                        Surface(
                            color = groupColor.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp),
                        ) {
                            Row(modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(GROUP_ICONS[safeIconIndex], contentDescription = null, modifier = Modifier.size(12.dp), tint = groupColor)
                                Text(group.name, style = MaterialTheme.typography.labelSmall, color = groupColor)
                            }
                        }
                    }
                }
                Row {
                    IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, contentDescription = "Edit") }
                    IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, contentDescription = "Delete") }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(item.word, style = MaterialTheme.typography.headlineMedium)
            Text(item.meaning, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            if (!item.language.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text("Language: \${item.language}", style = MaterialTheme.typography.bodySmall)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                repeat(5) { index ->
                    Text(if (index < item.mastery) "⭐" else "☆")
                }
            }
        }
    }
}


package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.draw.clip
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
    val filterCategories by viewModel.filterCategories.collectAsStateWithLifecycle()
    val sortOrder by viewModel.sortOrder.collectAsStateWithLifecycle()
    val showAddDialog by viewModel.showAddDialog.collectAsStateWithLifecycle()

    var editingItem by remember { mutableStateOf<Item?>(null) }
    var isSearching by remember { mutableStateOf(false) }
    var showSortMenu by remember { mutableStateOf(false) }

    val drawerState = LocalDrawerState.current
    val scope = rememberCoroutineScope()

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            if (isSearching) {
                TopAppBar(
                    title = {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = viewModel::updateSearchQuery,
                            modifier = Modifier.fillMaxWidth().padding(end = 8.dp),
                            placeholder = { Text("Search...", color = Color.White.copy(alpha = 0.6f)) },
                            singleLine = true,
                            colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.White,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                cursorColor = Color.White,
                            ),
                            trailingIcon = {
                                IconButton(onClick = {
                                    isSearching = false
                                    viewModel.updateSearchQuery("")
                                }) {
                                    Icon(Icons.Default.Close, contentDescription = "Close search", tint = Color.White)
                                }
                            }
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            } else {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                    title = {},
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                        }
                    },
                    actions = {
                        IconButton(onClick = { isSearching = true }) {
                            Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.White)
                        }
                        IconButton(onClick = { /* Handle Notifications */ }) {
                            Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = Color.White)
                        }
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = "Profile",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                )
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            androidx.compose.foundation.lazy.LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                item {
                    Box {
                        IconButton(onClick = { showSortMenu = true }) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = "Sort Options",
                                modifier = Modifier.padding(end = 4.dp),
                                tint = Color.White
                            )
                        }
                        DropdownMenu(
                            expanded = showSortMenu,
                            onDismissRequest = { showSortMenu = false }
                        ) {
                            com.example.SortOrder.values().forEach { order ->
                                DropdownMenuItem(
                                    text = { 
                                        val label = when (order) {
                                            com.example.SortOrder.DATE_ADDED_DESC -> "Newest First"
                                            com.example.SortOrder.DATE_ADDED_ASC -> "Oldest First"
                                            com.example.SortOrder.ALPHABETICAL_A_Z -> "A to Z"
                                            com.example.SortOrder.ALPHABETICAL_Z_A -> "Z to A"
                                            com.example.SortOrder.MASTERY_HIGH_LOW -> "Mastery (High to Low)"
                                            com.example.SortOrder.MASTERY_LOW_HIGH -> "Mastery (Low to High)"
                                        }
                                        Text(label, fontWeight = if (sortOrder == order) FontWeight.Bold else FontWeight.Normal)
                                    },
                                    onClick = {
                                        viewModel.updateSortOrder(order)
                                        showSortMenu = false
                                    }
                                )
                            }
                        }
                    }
                }
                item {
                    FilterChip(
                        selected = filterCategories.isEmpty(),
                        onClick = { viewModel.clearCategoryFilters() },
                        label = { Text("All") },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = Color.Transparent,
                            labelColor = Color.White.copy(alpha = 0.6f),
                            selectedContainerColor = Color.White.copy(alpha = 0.2f),
                            selectedLabelColor = Color.White
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = filterCategories.isEmpty(),
                            borderColor = Color.White.copy(alpha = 0.2f),
                            selectedBorderColor = Color.Transparent
                        ),
                        shape = CircleShape
                    )
                }
                items(customCategories) { category ->
                    FilterChip(
                        selected = filterCategories.contains(category),
                        onClick = { viewModel.toggleFilterCategory(category) },
                        label = { Text(category) },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = Color.Transparent,
                            labelColor = Color.White.copy(alpha = 0.6f),
                            selectedContainerColor = Color.White.copy(alpha = 0.2f),
                            selectedLabelColor = Color.White
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = filterCategories.contains(category),
                            borderColor = Color.White.copy(alpha = 0.2f),
                            selectedBorderColor = Color.Transparent
                        ),
                        shape = CircleShape
                    )
                }
            }
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(items, key = { it.id }) { item ->
                    ItemCard(
                        item = item,
                        groups = allGroups,
                        onEdit = {
                            editingItem = item
                            viewModel.setShowAddDialog(true)
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
                viewModel.setShowAddDialog(false)
                editingItem = null
            },
            onDismiss = {
                viewModel.setShowAddDialog(false)
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
        modifier = Modifier.fillMaxWidth().border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Badge(containerColor = Color.White.copy(alpha = 0.2f)) { Text(item.category, color = Color.White) }
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
                    IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.White.copy(alpha = 0.7f)) }
                    IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.White.copy(alpha = 0.7f)) }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(item.word, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(modifier = Modifier.height(4.dp))
            Text(item.meaning, style = MaterialTheme.typography.bodyLarge, color = Color.White.copy(alpha = 0.8f))
            if (!item.language.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Language: ${item.language}", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.6f))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                repeat(5) { index ->
                    Text(if (index < item.mastery) "⭐" else "☆", color = Color.White.copy(alpha = if (index < item.mastery) 1f else 0.3f))
                }
            }
        }
    }
}


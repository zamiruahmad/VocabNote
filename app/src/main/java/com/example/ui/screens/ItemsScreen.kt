package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.LexiconViewModel
import com.example.data.Item
import com.example.data.LexiconGroup
import com.example.ui.components.ItemModal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemsScreen(viewModel: LexiconViewModel, navController: NavController) {
    val items by viewModel.items.collectAsStateWithLifecycle()
    val allGroups by viewModel.allGroups.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    var showAddDialog by remember { mutableStateOf(false) }
    var editingItem by remember { mutableStateOf<Item?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lexicon (\${items.size})") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Item")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = viewModel::updateSearchQuery,
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                placeholder = { Text("Search words or meanings...") }
            )

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
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Badge(containerColor = MaterialTheme.colorScheme.primary) { Text(item.category) }
                    if (group != null) {
                        Badge(containerColor = groupColor) { Text(group.name) }
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

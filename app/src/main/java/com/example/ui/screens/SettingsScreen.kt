package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.LexiconViewModel
import com.example.data.LexiconGroup
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: LexiconViewModel) {
    val items by viewModel.items.collectAsStateWithLifecycle()
    val allGroups by viewModel.allGroups.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    
    var newGroupName by remember { mutableStateOf("") }
    
    // Some static colors for new groups
    val groupColors = listOf("#3b82f6", "#10b981", "#8b5cf6", "#f59e0b", "#ef4444")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Text("Stats", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(16.dp))
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        StatRow("Total Items", items.size.toString())
                        StatRow("Mastered Items (⭐️ 4+)", items.count { it.mastery >= 4 }.toString())
                        StatRow("Total Revisions", viewModel.settings.totalRevisions.toString())
                    }
                }
            }

            item {
                Text("Manage Groups", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = newGroupName,
                        onValueChange = { newGroupName = it },
                        label = { Text("Group Name") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    Button(
                        onClick = {
                            if (newGroupName.isNotBlank()) {
                                val randomColor = groupColors.random()
                                viewModel.insertGroup(LexiconGroup(name = newGroupName, colorHex = randomColor))
                                newGroupName = ""
                            }
                        },
                        enabled = newGroupName.isNotBlank()
                    ) {
                        Text("Add")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            items(allGroups) { group ->
                val groupItemCount = items.count { it.groupId == group.id }
                val color = try { Color(android.graphics.Color.parseColor(group.colorHex)) } catch(e: Exception) { Color.Gray }
                
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Box(modifier = Modifier.size(16.dp).clip(CircleShape).background(color))
                            Column {
                                Text(group.name, style = MaterialTheme.typography.titleMedium)
                                Text("\$groupItemCount items", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        IconButton(onClick = { viewModel.deleteGroup(group.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete Group")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontWeight = FontWeight.Bold)
    }
}


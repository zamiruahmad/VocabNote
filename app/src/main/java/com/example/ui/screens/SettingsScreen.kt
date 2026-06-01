package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Menu
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
import com.example.ui.components.GROUP_ICONS
import kotlinx.coroutines.launch

import com.example.ui.components.SidebarOutlinedIcon
import com.example.ui.LocalDrawerState
import kotlinx.coroutines.launch
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: LexiconViewModel) {
    val items by viewModel.items.collectAsStateWithLifecycle()
    val allGroups by viewModel.allGroups.collectAsStateWithLifecycle()
    val customCategories by viewModel.customCategories.collectAsStateWithLifecycle()
    val customGroupTypes by viewModel.customGroupTypes.collectAsStateWithLifecycle()
    val customLanguages by viewModel.customLanguages.collectAsStateWithLifecycle()
    
    var selectedTabIndex by remember { mutableStateOf(0) }
    
    val drawerState = LocalDrawerState.current
    val scope = rememberCoroutineScope()
    
    val tabs = listOf("General", "Groups", "Group Types", "Categories", "Languages")

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Settings & Management", fontWeight = FontWeight.Black, color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                        }
                    },
                    actions = {
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
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
                ScrollableTabRow(
                    selectedTabIndex = selectedTabIndex,
                    edgePadding = 16.dp,
                    containerColor = Color.Transparent,
                    contentColor = Color.White
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = { Text(title, color = if (selectedTabIndex == index) Color.White else Color.White.copy(alpha = 0.6f)) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (selectedTabIndex) {
                0 -> GeneralSettingsTab(viewModel)
                1 -> GroupsManagementTab(viewModel)
                2 -> StringListManagementTab("Group Type", customGroupTypes) { newList -> viewModel.updateGroupTypes(newList) }
                3 -> StringListManagementTab("Category", customCategories) { newList -> viewModel.updateCategories(newList) }
                4 -> StringListManagementTab("Language", customLanguages) { newList -> viewModel.updateLanguages(newList) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneralSettingsTab(viewModel: LexiconViewModel) {
    val items by viewModel.items.collectAsStateWithLifecycle()
    val showBottomNav by viewModel.showBottomNav.collectAsStateWithLifecycle()
    val activeTheme by viewModel.activeTheme.collectAsStateWithLifecycle()
    val activeFont by viewModel.activeFont.collectAsStateWithLifecycle()
    val activeAppLanguage by viewModel.activeAppLanguage.collectAsStateWithLifecycle()
    val darkMode by viewModel.darkMode.collectAsStateWithLifecycle()
    val dailyReminder by viewModel.dailyReminder.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            Text("Reminders", style = MaterialTheme.typography.titleLarge, color = Color.White)
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth().border(1.dp, Color.White.copy(alpha=0.1f), RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha=0.05f))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Daily Practice Reminder", style = MaterialTheme.typography.bodyLarge, color = Color.White)
                            Text("Get a notification to review your lexicon", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha=0.6f))
                        }
                        Switch(
                            checked = dailyReminder,
                            onCheckedChange = { viewModel.updateDailyReminder(it) }
                        )
                    }
                }
            }
        }

        item {
            Text("UI Configuration", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Show Bottom Navigation", style = MaterialTheme.typography.bodyLarge)
                        Switch(
                            checked = showBottomNav,
                            onCheckedChange = { viewModel.updateShowBottomNav(it) }
                        )
                    }

                    DropdownSettingRow("Theme", activeTheme, listOf("Blue", "Green", "Purple", "Orange", "Red")) { viewModel.updateActiveTheme(it) }
                    DropdownSettingRow("Font", activeFont, listOf("Default", "Serif", "Monospace")) { viewModel.updateActiveFont(it) }
                    DropdownSettingRow("Language", activeAppLanguage, listOf("English", "Bengali", "Spanish")) { viewModel.updateActiveAppLanguage(it) }
                    DropdownSettingRow("Dark Mode", darkMode, listOf("System", "Light", "Dark")) { viewModel.updateDarkMode(it) }
                }
            }
        }

        item {
            Text("Stats Overview", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatRow("Total Items", items.size.toString())
                    StatRow("Mastered Items (⭐️ 4+)", items.count { it.mastery >= 4 }.toString())
                    StatRow("Total Revisions Completed", viewModel.settings.totalRevisions.toString())
                }
            }
        }

        item {
            Text("Data Management", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))
            
            val context = LocalContext.current
            val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                uri?.let {
                    viewModel.importData(context, it) { success, msg ->
                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                    }
                }
            }
            
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Import your vocabulary from a CSV or JSON file. For CSV, ensure you have 'word' and 'meaning' header columns.", style = MaterialTheme.typography.bodyMedium)
                    Button(onClick = { launcher.launch("*/*") }, modifier = Modifier.fillMaxWidth()) {
                        Text("Import Data")
                    }
                }
            }
        }
    }
}

@Composable
fun DropdownSettingRow(label: String, currentValue: String, options: List<String>, onSelection: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Box {
            TextButton(onClick = { expanded = true }) {
                Text(currentValue)
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = { 
                            onSelection(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupsManagementTab(viewModel: LexiconViewModel) {
    val allGroups by viewModel.allGroups.collectAsStateWithLifecycle()
    val customGroupTypes by viewModel.customGroupTypes.collectAsStateWithLifecycle()
    val items by viewModel.items.collectAsStateWithLifecycle()
    var newGroupName by remember { mutableStateOf("") }
    var selectedGroupType by remember { mutableStateOf("") }
    var groupTypeExpanded by remember { mutableStateOf(false) }
    var selectedIconIndex by remember { mutableStateOf(0) }
    val groupColors = listOf("#3b82f6", "#10b981", "#8b5cf6", "#f59e0b", "#ef4444")
    
    // Default selected group type when loaded if empty
    LaunchedEffect(customGroupTypes) {
        if (selectedGroupType.isEmpty() && customGroupTypes.isNotEmpty()) {
            selectedGroupType = customGroupTypes.first()
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedTextField(
                        value = newGroupName,
                        onValueChange = { newGroupName = it },
                        label = { Text("New Group Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    
                    ExposedDropdownMenuBox(
                        expanded = groupTypeExpanded,
                        onExpandedChange = { groupTypeExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = selectedGroupType,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Group Type") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = groupTypeExpanded) },
                            modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = groupTypeExpanded,
                            onDismissRequest = { groupTypeExpanded = false }
                        ) {
                            val availableGroupTypes = if (customGroupTypes.isEmpty()) listOf("Movie", "Song", "Comics", "Newspaper", "Poster", "Drama", "Other") else customGroupTypes
                            availableGroupTypes.forEach { groupType ->
                                DropdownMenuItem(
                                    text = { Text(groupType) },
                                    onClick = {
                                        selectedGroupType = groupType
                                        groupTypeExpanded = false
                                    }
                                )
                            }
                        }
                    }
                    
                    Text("Select Icon:", style = MaterialTheme.typography.bodyMedium)
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        items(GROUP_ICONS.size) { index ->
                            val isSelected = index == selectedIconIndex
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                                    .clickable { selectedIconIndex = index },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = GROUP_ICONS[index],
                                    contentDescription = "Icon $index",
                                    tint = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Button(
                        onClick = {
                            if (newGroupName.isNotBlank()) {
                                viewModel.insertGroup(LexiconGroup(name = newGroupName, type = selectedGroupType.ifEmpty { "Other" }, colorHex = groupColors.random(), iconIndex = selectedIconIndex))
                                newGroupName = ""
                                selectedIconIndex = 0
                            }
                        },
                        modifier = Modifier.align(Alignment.End),
                        enabled = newGroupName.isNotBlank()
                    ) {
                        Text("Add Group")
                    }
                }
            }
        }
        
        items(allGroups) { group ->
            val groupItemCount = items.count { it.groupId == group.id }
            val color = try { Color(android.graphics.Color.parseColor(group.colorHex)) } catch(e: Exception) { Color.Gray }
            val safeIconIndex = group.iconIndex.coerceIn(0, GROUP_ICONS.lastIndex)
            
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Box(
                            modifier = Modifier.size(48.dp).clip(CircleShape).background(color.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(GROUP_ICONS[safeIconIndex], contentDescription = null, tint = color)
                        }
                        Column {
                            Text(group.name, style = MaterialTheme.typography.titleMedium)
                            Text("${group.type} • $groupItemCount items", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    IconButton(onClick = { viewModel.deleteGroup(group.id) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Group", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}

@Composable
fun StringListManagementTab(itemName: String, currentList: List<String>, onUpdate: (List<String>) -> Unit) {
    var newItemName by remember { mutableStateOf("") }
    
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newItemName,
                        onValueChange = { newItemName = it },
                        label = { Text("New $itemName") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    Button(
                        onClick = {
                            if (newItemName.isNotBlank() && !currentList.contains(newItemName.trim())) {
                                onUpdate(currentList + newItemName.trim())
                                newItemName = ""
                            }
                        },
                        enabled = newItemName.isNotBlank()
                    ) {
                        Text("Add")
                    }
                }
            }
        }
        
        items(currentList) { itemValue ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(itemValue, style = MaterialTheme.typography.bodyLarge)
                    IconButton(onClick = { 
                        onUpdate(currentList.filter { it != itemValue })
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
        
        if (currentList.isEmpty()) {
            item {
                Text("No ${itemName}s found.", modifier = Modifier.padding(16.dp))
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


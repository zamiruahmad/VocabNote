package com.example.ui.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.Item
import com.example.data.LexiconGroup

import com.example.ui.components.GROUP_ICONS
import androidx.compose.ui.graphics.Color

val CATEGORIES = listOf(
    "Vocabulary", "Grammar", "Proverb", "Sentence",
    "Expression", "Idiom", "Question", "Other"
)

val LANGUAGES = listOf(
    "English", "Bengali", "Arabic", "French",
    "Spanish", "German", "Chinese", "Japanese"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemModal(
    initialItem: Item?,
    groups: List<LexiconGroup>,
    customCategories: List<String>,
    customLanguages: List<String>,
    onSave: (Item) -> Unit,
    onDismiss: () -> Unit
) {
    var category by remember { mutableStateOf(initialItem?.category ?: customCategories.firstOrNull() ?: "Vocabulary") }
    var expandedCategory by remember { mutableStateOf(false) }

    var word by remember { mutableStateOf(initialItem?.word ?: "") }
    var meaning by remember { mutableStateOf(initialItem?.meaning ?: "") }

    var language by remember { mutableStateOf(initialItem?.language ?: customLanguages.firstOrNull() ?: "English") }
    var expandedLanguage by remember { mutableStateOf(false) }

    var source by remember { mutableStateOf(initialItem?.source ?: "") }
    var imageUrl by remember { mutableStateOf(initialItem?.imageUrl ?: "") }
    var note by remember { mutableStateOf(initialItem?.note ?: "") }
    var link by remember { mutableStateOf(initialItem?.link ?: "") }
    var groupId by remember { mutableStateOf(initialItem?.groupId) }
    var expandedGroup by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            if (uri != null) {
                imageUrl = uri.toString()
            }
        }
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                TopAppBar(
                    title = { Text(if (initialItem == null) "Add Item" else "Edit Item") },
                    actions = {
                        TextButton(onClick = onDismiss) { Text("Cancel") }
                    }
                )

                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Row for Category (+) and Language (En > Bn)
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Category Selection Button Card with (+) inside as sketched
                            Box(modifier = Modifier.weight(1f)) {
                                ExposedDropdownMenuBox(
                                    expanded = expandedCategory,
                                    onExpandedChange = { expandedCategory = !expandedCategory }
                                ) {
                                    OutlinedTextField(
                                        value = category,
                                        onValueChange = {},
                                        readOnly = true,
                                        leadingIcon = { Icon(Icons.Default.Add, contentDescription = "Category") },
                                        label = { Text("Category") },
                                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory) },
                                        modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
                                        singleLine = true
                                    )
                                    ExposedDropdownMenu(
                                        expanded = expandedCategory,
                                        onDismissRequest = { expandedCategory = false }
                                    ) {
                                        customCategories.forEach { cat ->
                                            DropdownMenuItem(
                                                text = { Text(cat) },
                                                onClick = {
                                                    category = cat
                                                    expandedCategory = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            // Language Selection
                            Box(modifier = Modifier.weight(1.2f)) {
                                ExposedDropdownMenuBox(
                                    expanded = expandedLanguage,
                                    onExpandedChange = { expandedLanguage = !expandedLanguage }
                                ) {
                                    OutlinedTextField(
                                        value = language,
                                        onValueChange = {},
                                        readOnly = true,
                                        label = { Text("Language") },
                                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedLanguage) },
                                        modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
                                        singleLine = true
                                    )
                                    ExposedDropdownMenu(
                                        expanded = expandedLanguage,
                                        onDismissRequest = { expandedLanguage = false }
                                    ) {
                                        customLanguages.forEach { lang ->
                                            DropdownMenuItem(
                                                text = { Text(lang) },
                                                onClick = {
                                                    language = lang
                                                    expandedLanguage = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Word / Phrase / Sentence
                    item {
                        OutlinedTextField(
                            value = word,
                            onValueChange = { word = it },
                            label = { Text("Word / Sentence / Phrase *") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Meaning
                    item {
                        OutlinedTextField(
                            value = meaning,
                            onValueChange = { meaning = it },
                            label = { Text("Meaning *") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3
                        )
                    }

                    // Combined Section for (Pic, link, Note, Source) in single area
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp).fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                // Picture picker button box labeled + Pic
                                Box(
                                    modifier = Modifier
                                        .size(88.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(MaterialTheme.colorScheme.surface)
                                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                                        .clickable { photoPickerLauncher.launch("image/*") },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (imageUrl.isBlank()) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Icon(Icons.Default.Add, contentDescription = "Add Pic", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text("Pic", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    } else {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(4.dp)) {
                                            Icon(Icons.Default.Image, contentDescription = "Has Picture", tint = MaterialTheme.colorScheme.primary)
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text("Selected", style = MaterialTheme.typography.labelSmall, maxLines = 1, textAlign = TextAlign.Center)
                                        }
                                    }
                                }

                                // Personal Note field (large) encompassing link and source
                                OutlinedTextField(
                                    value = note,
                                    onValueChange = { note = it },
                                    label = { Text("Note / Links / Source") },
                                    modifier = Modifier
                                        .weight(1f)
                                        .defaultMinSize(minHeight = 88.dp),
                                    maxLines = 5,
                                    textStyle = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }

                    // Add group/subgroup input box with leading (+) sign
                    item {
                        ExposedDropdownMenuBox(
                            expanded = expandedGroup,
                            onExpandedChange = { expandedGroup = !expandedGroup }
                        ) {
                            val selectedGroup = groups.find { it.id == groupId }
                            val leadingIconVector = if (selectedGroup != null) {
                                val safeIndex = selectedGroup.iconIndex.coerceIn(0, GROUP_ICONS.lastIndex)
                                GROUP_ICONS[safeIndex]
                            } else {
                                Icons.Default.Add
                            }
                            
                            OutlinedTextField(
                                value = selectedGroup?.name ?: "Add / Select Group",
                                onValueChange = {},
                                readOnly = true,
                                leadingIcon = { 
                                    if (selectedGroup != null) {
                                        val c = try { Color(android.graphics.Color.parseColor(selectedGroup.colorHex)) } catch(e:Exception){ MaterialTheme.colorScheme.primary }
                                        Icon(leadingIconVector, contentDescription = "Group Icon", tint = c)
                                    } else {
                                        Icon(leadingIconVector, contentDescription = "Add Group")
                                    }
                                },
                                label = { Text("Group / Subgroup") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedGroup) },
                                modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = expandedGroup,
                                onDismissRequest = { expandedGroup = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("None (Ungrouped)") },
                                    onClick = {
                                        groupId = null
                                        expandedGroup = false
                                    }
                                )
                                groups.forEach { grp ->
                                    val safeIdx = grp.iconIndex.coerceIn(0, GROUP_ICONS.lastIndex)
                                    val c = try { Color(android.graphics.Color.parseColor(grp.colorHex)) } catch(e:Exception){ MaterialTheme.colorScheme.onSurface }
                                    DropdownMenuItem(
                                        text = { Text(grp.name) },
                                        leadingIcon = {
                                            Icon(GROUP_ICONS[safeIdx], contentDescription = null, tint = c)
                                        },
                                        onClick = {
                                            groupId = grp.id
                                            expandedGroup = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                // Centered Save Button at the bottom
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = {
                            if (word.isNotBlank() && meaning.isNotBlank()) {
                                val newItem = Item(
                                    id = initialItem?.id ?: 0L,
                                    category = category,
                                    word = word,
                                    meaning = meaning,
                                    language = language.ifBlank { null },
                                    source = source.ifBlank { null },
                                    imageUrl = imageUrl.ifBlank { null },
                                    note = note.ifBlank { null },
                                    link = link.ifBlank { null },
                                    groupId = groupId,
                                    mastery = initialItem?.mastery ?: 0,
                                    nextRevision = initialItem?.nextRevision ?: System.currentTimeMillis(),
                                    reviewCount = initialItem?.reviewCount ?: 0,
                                    createdAt = initialItem?.createdAt ?: System.currentTimeMillis()
                                )
                                onSave(newItem)
                            }
                        },
                        enabled = word.isNotBlank() && meaning.isNotBlank(),
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Save", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
    }
}

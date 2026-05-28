package com.example.ui.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.Item
import com.example.data.LexiconGroup

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
    onSave: (Item) -> Unit,
    onDismiss: () -> Unit
) {
    var category by remember { mutableStateOf(initialItem?.category ?: "Vocabulary") }
    var expandedCategory by remember { mutableStateOf(false) }

    var word by remember { mutableStateOf(initialItem?.word ?: "") }
    var meaning by remember { mutableStateOf(initialItem?.meaning ?: "") }

    var language by remember { mutableStateOf(initialItem?.language ?: "English") }
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
                // You usually need to request persistable permission here or copy the file, 
                // but just storing the URI string for visual layout as requested.
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
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text("Save")
                        }
                    }
                )

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        ExposedDropdownMenuBox(
                            expanded = expandedCategory,
                            onExpandedChange = { expandedCategory = !expandedCategory }
                        ) {
                            OutlinedTextField(
                                value = category,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Category") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory) },
                                modifier = Modifier.menuAnchor().fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = expandedCategory,
                                onDismissRequest = { expandedCategory = false }
                            ) {
                                CATEGORIES.forEach { cat ->
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

                    item {
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
                                modifier = Modifier.menuAnchor().fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = expandedLanguage,
                                onDismissRequest = { expandedLanguage = false }
                            ) {
                                LANGUAGES.forEach { lang ->
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

                    item {
                        OutlinedTextField(
                            value = word,
                            onValueChange = { word = it },
                            label = { Text("Word / Sentence / Phrase *") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = meaning,
                            onValueChange = { meaning = it },
                            label = { Text("Meaning *") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3
                        )
                    }

                    item {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                Text("Additional Details", style = MaterialTheme.typography.titleMedium)
                                
                                OutlinedTextField(
                                    value = source,
                                    onValueChange = { source = it },
                                    label = { Text("Source") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedTextField(
                                        value = imageUrl,
                                        onValueChange = { imageUrl = it },
                                        label = { Text("Pic (URL or Pick Image)") },
                                        modifier = Modifier.weight(1f)
                                    )
                                    IconButton(
                                        onClick = {
                                            photoPickerLauncher.launch("image/*")
                                        }
                                    ) {
                                        Icon(Icons.Default.Image, contentDescription = "Pick Image")
                                    }
                                }

                                OutlinedTextField(
                                    value = note,
                                    onValueChange = { note = it },
                                    label = { Text("Personal Note") },
                                    modifier = Modifier.fillMaxWidth(),
                                    minLines = 2
                                )

                                OutlinedTextField(
                                    value = link,
                                    onValueChange = { link = it },
                                    label = { Text("Link (URL)") },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }

                    item {
                        ExposedDropdownMenuBox(
                            expanded = expandedGroup,
                            onExpandedChange = { expandedGroup = !expandedGroup }
                        ) {
                            val selectedGroup = groups.find { it.id == groupId }
                            OutlinedTextField(
                                value = selectedGroup?.name ?: "None",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Add group") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedGroup) },
                                modifier = Modifier.menuAnchor().fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = expandedGroup,
                                onDismissRequest = { expandedGroup = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("None") },
                                    onClick = {
                                        groupId = null
                                        expandedGroup = false
                                    }
                                )
                                groups.forEach { grp ->
                                    DropdownMenuItem(
                                        text = { Text(grp.name) },
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
            }
        }
    }
}

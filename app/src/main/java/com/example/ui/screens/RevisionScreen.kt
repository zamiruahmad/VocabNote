package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.LexiconViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RevisionScreen(viewModel: LexiconViewModel) {
    val revisionItems by viewModel.revisionItems.collectAsStateWithLifecycle()
    
    var currentIndex by remember { mutableStateOf(0) }
    var isFlipped by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daily Revision") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (revisionItems.isEmpty() || currentIndex >= revisionItems.size) {
                Spacer(modifier = Modifier.weight(1f))
                Text("All caught up for today! 🎉", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { currentIndex = 0; isFlipped = false }) {
                    Text("Refresh")
                }
                Spacer(modifier = Modifier.weight(1f))
            } else {
                val currentItem = revisionItems[currentIndex]
                
                Text(
                    text = "Card \${currentIndex + 1} of \${revisionItems.size}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(32.dp))
                
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clickable { isFlipped = !isFlipped },
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
                            Badge(containerColor = MaterialTheme.colorScheme.primary) { Text(currentItem.category) }
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            if (!isFlipped) {
                                Text(
                                    text = currentItem.word,
                                    style = MaterialTheme.typography.displaySmall,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Text("Tap to reveal", style = MaterialTheme.typography.bodySmall)
                            } else {
                                Text(
                                    text = currentItem.word,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = currentItem.meaning,
                                    style = MaterialTheme.typography.headlineSmall,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Row {
                                    repeat(5) { index ->
                                        Text(if (index < currentItem.mastery) "⭐" else "☆")
                                    }
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                if (isFlipped) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = { 
                                viewModel.processRevision(currentItem, -1, 1.0)
                                isFlipped = false
                                currentIndex++
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) { Text("Again") }
                        
                        Button(
                            onClick = { 
                                viewModel.processRevision(currentItem, 0, 1.0)
                                isFlipped = false
                                currentIndex++
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                        ) { Text("Hard") }
                        
                        Button(
                            onClick = { 
                                viewModel.processRevision(currentItem, 1, 1.0)
                                isFlipped = false
                                currentIndex++
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) { Text("Good") }
                        
                        Button(
                            onClick = { 
                                viewModel.processRevision(currentItem, 1, 1.5)
                                isFlipped = false
                                currentIndex++
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                        ) { Text("Easy") }
                    }
                } else {
                    Spacer(modifier = Modifier.height(48.dp))
                }
            }
        }
    }
}


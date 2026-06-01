package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Menu
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.background
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.LexiconViewModel
import com.example.data.Item

import com.example.ui.components.SidebarOutlinedIcon
import com.example.ui.LocalDrawerState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RevisionScreen(viewModel: LexiconViewModel) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    
    val drawerState = LocalDrawerState.current
    val scope = rememberCoroutineScope()
    
    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Daily Revision & Quiz") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    actions = {
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = "Profile",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
                TabRow(selectedTabIndex = selectedTabIndex, containerColor = Color.Transparent) {
                    Tab(
                        selected = selectedTabIndex == 0,
                        onClick = { selectedTabIndex = 0 },
                        text = { Text("Flashcards") }
                    )
                    Tab(
                        selected = selectedTabIndex == 1,
                        onClick = { selectedTabIndex = 1 },
                        text = { Text("Quiz Mode") }
                    )
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (selectedTabIndex == 0) {
                RevisionFlashcards(viewModel)
            } else {
                RevisionQuiz(viewModel)
            }
        }
    }
}

@Composable
fun RevisionFlashcards(viewModel: LexiconViewModel) {
    val revisionItems by viewModel.revisionItems.collectAsStateWithLifecycle()
    var currentIndex by remember { mutableStateOf(0) }
    var isFlipped by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
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

@Composable
fun RevisionQuiz(viewModel: LexiconViewModel) {
    val allItems by viewModel.items.collectAsStateWithLifecycle()
    var isQuizActive by remember { mutableStateOf(false) }
    var quizItems by remember { mutableStateOf<List<Item>>(emptyList()) }
    var currentIndex by remember { mutableStateOf(0) }
    var score by remember { mutableStateOf(0) }
    var showResult by remember { mutableStateOf(false) }
    var currentOptions by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedOption by remember { mutableStateOf<String?>(null) }
    var hasAnswered by remember { mutableStateOf(false) }

    fun generateOptions(correctItem: Item) {
        val distractors = allItems.filter { it.id != correctItem.id }.shuffled().take(3).map { it.meaning }
        val allOptions = (distractors + correctItem.meaning).shuffled()
        currentOptions = allOptions
        selectedOption = null
        hasAnswered = false
    }

    fun startQuiz() {
        val selected = allItems.shuffled().take(10)
        if (selected.size >= 4) {
            quizItems = selected
            currentIndex = 0
            score = 0
            isQuizActive = true
            showResult = false
            generateOptions(quizItems[0])
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        if (!isQuizActive && !showResult) {
            Spacer(modifier = Modifier.weight(1f))
            Text("Test Your Knowledge", style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = { startQuiz() },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = allItems.size >= 4
            ) {
                Text("Start Multiple Choice Quiz (Needs >= 4 items)")
            }
            Spacer(modifier = Modifier.weight(1f))
        } else if (showResult) {
            Spacer(modifier = Modifier.weight(1f))
            Text("Quiz Completed!", style = MaterialTheme.typography.headlineLarge, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))
            Text("Score: \$score / \${quizItems.size}", style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = { startQuiz() }, modifier = Modifier.fillMaxWidth()) { Text("Try Again") }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(onClick = { isQuizActive = false; showResult = false }, modifier = Modifier.fillMaxWidth()) { Text("Exit Quiz") }
            Spacer(modifier = Modifier.weight(1f))
        } else {
            val currentItem = quizItems[currentIndex]
            
            Text("Question \${currentIndex + 1} of \${quizItems.size}", style = MaterialTheme.typography.labelLarge)
            Spacer(modifier = Modifier.height(16.dp))
            
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("What is the meaning of:", style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(currentItem.word, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            currentOptions.forEach { option ->
                val isCorrect = option == currentItem.meaning
                val isSelected = option == selectedOption
                
                val containerColor = if (hasAnswered) {
                    if (isCorrect) MaterialTheme.colorScheme.tertiaryContainer
                    else if (isSelected) MaterialTheme.colorScheme.errorContainer
                    else MaterialTheme.colorScheme.surfaceVariant
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                }

                Card(
                    onClick = {
                        if (!hasAnswered) {
                            selectedOption = option
                            hasAnswered = true
                            if (isCorrect) score++
                        }
                    },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = containerColor)
                ) {
                    Text(option, modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.bodyLarge)
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            if (hasAnswered) {
                Button(
                    onClick = {
                        if (currentIndex < quizItems.size - 1) {
                            currentIndex++
                            generateOptions(quizItems[currentIndex])
                        } else {
                            showResult = true
                            isQuizActive = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Text(if (currentIndex < quizItems.size - 1) "Next Question" else "Finish")
                }
            }
        }
    }
}


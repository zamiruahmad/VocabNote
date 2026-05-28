package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.LexiconViewModel
import com.example.data.Item
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(viewModel: LexiconViewModel) {
    val allItems by viewModel.items.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    
    var isQuizActive by remember { mutableStateOf(false) }
    var quizItems by remember { mutableStateOf<List<Item>>(emptyList()) }
    var currentIndex by remember { mutableStateOf(0) }
    var score by remember { mutableStateOf(0) }
    var showResult by remember { mutableStateOf(false) }
    
    // Each question has 4 options (strings)
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quiz Mode") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
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
                OutlinedButton(onClick = { isQuizActive = false; showResult = false }, modifier = Modifier.fillMaxWidth()) { Text("Back to Menu") }
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
                        if (isCorrect) MaterialTheme.colorScheme.tertiaryContainer // Green-ish
                        else if (isSelected) MaterialTheme.colorScheme.errorContainer // Red-ish
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
}


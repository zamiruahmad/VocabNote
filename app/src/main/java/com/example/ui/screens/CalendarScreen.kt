package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.LexiconViewModel
import com.example.data.Item
import java.text.SimpleDateFormat
import java.util.*

import com.example.ui.components.SidebarOutlinedIcon
import com.example.ui.LocalDrawerState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(viewModel: LexiconViewModel) {
    val items by viewModel.items.collectAsStateWithLifecycle()
    val allGroups by viewModel.allGroups.collectAsStateWithLifecycle()

    var currentMonth by remember { mutableStateOf(Calendar.getInstance()) }
    var selectedDate by remember { mutableStateOf<Calendar?>(null) }

    val drawerState = LocalDrawerState.current
    val scope = rememberCoroutineScope()

    val monthFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
    val monthTitle = monthFormat.format(currentMonth.time)

    // Compute calendar grid
    val daysInMonth = currentMonth.getActualMaximum(Calendar.DAY_OF_MONTH)
    val clone = currentMonth.clone() as Calendar
    clone.set(Calendar.DAY_OF_MONTH, 1)
    val firstDayOfWeek = clone.get(Calendar.DAY_OF_WEEK) - 1 // 0 for Sunday

    val emptyDaysBefore = firstDayOfWeek

    // Display items
    val displayedItems = remember(items, currentMonth, selectedDate) {
        items.filter { item ->
            val cal = Calendar.getInstance().apply { timeInMillis = item.createdAt }
            if (selectedDate != null) {
                cal.get(Calendar.YEAR) == selectedDate!!.get(Calendar.YEAR) &&
                cal.get(Calendar.MONTH) == selectedDate!!.get(Calendar.MONTH) &&
                cal.get(Calendar.DAY_OF_MONTH) == selectedDate!!.get(Calendar.DAY_OF_MONTH)
            } else {
                cal.get(Calendar.YEAR) == currentMonth.get(Calendar.YEAR) &&
                cal.get(Calendar.MONTH) == currentMonth.get(Calendar.MONTH)
            }
        }.sortedByDescending { it.createdAt }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Calendar") },
                navigationIcon = {
                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                        SidebarOutlinedIcon(contentDescription = "Menu")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    currentMonth = (currentMonth.clone() as Calendar).apply { add(Calendar.MONTH, -1) }
                    selectedDate = null
                }) { Icon(Icons.Default.ChevronLeft, "Previous") }
                
                Text(monthTitle, style = MaterialTheme.typography.titleLarge)
                
                IconButton(onClick = {
                    currentMonth = (currentMonth.clone() as Calendar).apply { add(Calendar.MONTH, 1) }
                    selectedDate = null
                }) { Icon(Icons.Default.ChevronRight, "Next") }
            }

            // Headers
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat").forEach { day ->
                    Text(
                        text = day,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            
            // Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            ) {
                items(emptyDaysBefore) {
                    Box(modifier = Modifier.aspectRatio(1f))
                }
                items(daysInMonth) { dayIndex ->
                    val day = dayIndex + 1
                    val dateCal = currentMonth.clone() as Calendar
                    dateCal.set(Calendar.DAY_OF_MONTH, day)

                    val isToday = isSameDay(dateCal, Calendar.getInstance())
                    val isSelected = selectedDate != null && isSameDay(dateCal, selectedDate!!)

                    val dayItems = items.filter { isSameDay(it.createdAt, dateCal) }

                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .padding(2.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                            .border(
                                1.dp,
                                if (isToday) MaterialTheme.colorScheme.primary else Color.Transparent,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable {
                                selectedDate = if (isSelected) null else dateCal
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = day.toString(),
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                            )
                            if (dayItems.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                    dayItems.take(3).forEach { _ ->
                                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(MaterialTheme.colorScheme.secondary))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()

            val titleText = if (selectedDate != null) {
                "Items on " + SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(selectedDate!!.time)
            } else {
                "Items in \$monthTitle"
            }
            Text(
                text = titleText,
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.titleMedium
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(displayedItems) { item ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(item.word, style = MaterialTheme.typography.titleMedium)
                            Text(item.meaning, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}

private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
           cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}

private fun isSameDay(timestamp: Long, cal2: Calendar): Boolean {
    val cal1 = Calendar.getInstance().apply { timeInMillis = timestamp }
    return isSameDay(cal1, cal2)
}


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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Menu
import kotlinx.coroutines.launch
import com.example.ui.LocalDrawerState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material.icons.outlined.SportsScore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.LexiconViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(viewModel: LexiconViewModel) {
    val items by viewModel.items.collectAsStateWithLifecycle()

    var currentMonth by remember { mutableStateOf(Calendar.getInstance()) }
    var selectedDate by remember { mutableStateOf(Calendar.getInstance()) }
    val drawerState = LocalDrawerState.current
    val scope = rememberCoroutineScope()

    // Date Formatters
    val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
    val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())
    
    val selectedDateString = dateFormat.format(selectedDate.time)
    val selectedDayString = dayFormat.format(selectedDate.time)

    // Compute calendar grid
    val daysInMonth = currentMonth.getActualMaximum(Calendar.DAY_OF_MONTH)
    val clone = currentMonth.clone() as Calendar
    clone.set(Calendar.DAY_OF_MONTH, 1)
    val firstDayOfWeek = clone.get(Calendar.DAY_OF_WEEK) - 1 // 0 for Sunday
    val emptyDaysBefore = firstDayOfWeek

    // Display items
    val displayedItems = remember(items, selectedDate) {
        items.filter { item ->
            val cal = Calendar.getInstance().apply { timeInMillis = item.createdAt }
            cal.get(Calendar.YEAR) == selectedDate.get(Calendar.YEAR) &&
            cal.get(Calendar.MONTH) == selectedDate.get(Calendar.MONTH) &&
            cal.get(Calendar.DAY_OF_MONTH) == selectedDate.get(Calendar.DAY_OF_MONTH)
        }.sortedByDescending { it.createdAt }
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                title = { Text(text = "Calendar", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // --- Top Row ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = selectedDateString,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = selectedDayString,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    color = Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.padding(bottom = 1.dp)
                )
            }
            
            Row {
                IconButton(
                    onClick = {
                        currentMonth = (currentMonth.clone() as Calendar).apply { add(Calendar.MONTH, -1) }
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(Icons.Default.ChevronLeft, contentDescription = "Previous Month", tint = Color.White)
                }
                IconButton(
                    onClick = {
                        currentMonth = (currentMonth.clone() as Calendar).apply { add(Calendar.MONTH, 1) }
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(Icons.Default.ChevronRight, contentDescription = "Next Month", tint = Color.White)
                }
            }
        }

        // --- Day Headers ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat").forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // --- Calendar Grid ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
        ) {
            val totalCells = emptyDaysBefore + daysInMonth
            val rows = (totalCells + 6) / 7
            
            for (row in 0 until rows) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    for (col in 0 until 7) {
                        val cellIndex = row * 7 + col
                        val dayIndex = cellIndex - emptyDaysBefore
                        
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(2.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (dayIndex in 0 until daysInMonth) {
                                val day = dayIndex + 1
                                val dateCal = currentMonth.clone() as Calendar
                                dateCal.set(Calendar.DAY_OF_MONTH, day)

                                val isSelected = isSameDay(dateCal, selectedDate)
                                val dayItemsCount = items.count { isSameDay(it.createdAt, dateCal) }
                                
                                val dotColors = listOf(Color(0xFFFF5252), Color(0xFF43A047), Color(0xFF29B6F6), Color(0xFFAB47BC))

                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape)
                                        .background(if (isSelected) Color.White.copy(alpha = 0.2f) else Color.Transparent)
                                        .clickable {
                                            selectedDate = dateCal.clone() as Calendar
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = day.toString(),
                                            color = Color.White,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 15.sp
                                        )
                                        if (dayItemsCount > 0 && !isSelected) {
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                                val colorToUse = dotColors[dayIndex % dotColors.size]
                                                Box(
                                                    modifier = Modifier
                                                        .size(4.dp)
                                                        .clip(CircleShape)
                                                        .background(colorToUse)
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
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- Today's Item Header ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Today's Item",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Color.White
                )
            }
            
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.2f))
                    .clickable { },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Item",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- Task List ---
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 120.dp) // Bottom padding for navigation bar
        ) {
            if (displayedItems.isEmpty()) {
                item {
                    Text(
                        text = "No items for this day.",
                        color = Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            } else {
                items(displayedItems.size) { index ->
                    val item = displayedItems[index]
                    val timeFormat = SimpleDateFormat("dd MMMM yyyy, h:mm a", Locale.getDefault())
                    val timeText = timeFormat.format(item.createdAt)
                    
                    val typeText = item.category.ifEmpty { "Uncategorized" }

                    Card(
                        modifier = Modifier.fillMaxWidth().border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f))
                    ) {
                        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                            Text(
                                text = typeText,
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.6f),
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                            Text(
                                text = item.word,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            Text(
                                text = timeText, // using created time as the formatted time
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.5f)
                            )
                        }
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



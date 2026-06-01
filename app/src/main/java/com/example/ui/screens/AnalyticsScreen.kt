package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.background
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.LexiconViewModel
import com.example.ui.LocalDrawerState
import com.example.ui.components.SidebarOutlinedIcon
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(viewModel: LexiconViewModel) {
    val items by viewModel.items.collectAsStateWithLifecycle()
    val groups by viewModel.allGroups.collectAsStateWithLifecycle()

    val drawerState = LocalDrawerState.current
    val scope = rememberCoroutineScope()

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text("Analytics", fontWeight = FontWeight.Black) },
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
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Column {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Overview",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = MaterialTheme.colorScheme.surface,
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text("This Week", style = MaterialTheme.typography.labelMedium)
                                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            OverviewCard(
                                count = items.size,
                                label = "Total Items",
                                chartColor = Color(0xFF8B5CF6),
                                backgroundColor = Color(0xFFF3E8FF),
                                modifier = Modifier.weight(1f)
                            )
                            OverviewCard(
                                count = items.count { it.mastery >= 5 },
                                label = "Mastered",
                                chartColor = Color(0xFFF97316),
                                backgroundColor = Color(0xFFFFEDD5),
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            OverviewCard(
                                count = groups.size,
                                label = "Groups",
                                chartColor = Color(0xFF06B6D4),
                                backgroundColor = Color(0xFFCFFAFE),
                                modifier = Modifier.weight(1f)
                            )
                            OverviewCard(
                                count = items.map { it.category }.distinct().size,
                                label = "Categories",
                                chartColor = Color(0xFF10B981),
                                backgroundColor = Color(0xFFD1FAE5),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.PieChart, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                                Text("By Category", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            val categoryCounts = items.groupingBy { it.category }.eachCount()
                            categoryCounts.entries.sortedByDescending { it.value }.forEach { (category, count) ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(category, style = MaterialTheme.typography.bodyMedium)
                                    Text("\$count", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.PieChart, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
                                Text("By Group", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            val groupCounts = items.groupingBy { it.groupId }.eachCount()
                            groupCounts.entries.sortedByDescending { it.value }.forEach { (groupId, count) ->
                                val groupName = groups.find { it.id == groupId }?.name ?: "Ungrouped"
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(groupName, style = MaterialTheme.typography.bodyMedium)
                                    Text("\$count", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
    }
}

@Composable
fun OverviewCard(
    count: Int,
    label: String,
    chartColor: Color,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = count.toString(),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black.copy(alpha = 0.8f)
                )
                Canvas(modifier = Modifier.weight(1f).height(24.dp).padding(start = 16.dp)) {
                    val path = Path()
                    path.moveTo(0f, size.height / 2)
                    path.quadraticTo(size.width * 0.2f, 0f, size.width * 0.4f, size.height / 2)
                    path.quadraticTo(size.width * 0.6f, size.height, size.width * 0.8f, size.height / 2)
                    path.quadraticTo(size.width * 0.9f, 0f, size.width, size.height / 2)
                    
                    drawPath(
                        path = path,
                        color = chartColor,
                        style = Stroke(width = 4f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black.copy(alpha = 0.6f)
                )
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = Color.Black.copy(alpha = 0.4f)
                )
            }
        }
    }
}
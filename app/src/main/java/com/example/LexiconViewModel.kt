package com.example

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.Item
import com.example.data.LexiconGroup
import com.example.data.LexiconRepository
import com.example.data.SettingsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

class LexiconViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val repository = LexiconRepository(database.itemDao(), database.groupDao())
    val settings = SettingsManager(application)

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery
    
    private val _filterCategory = MutableStateFlow<String?>(null)
    val filterCategory: StateFlow<String?> = _filterCategory

    private val _filterGroupId = MutableStateFlow<Long?>(null)
    val filterGroupId: StateFlow<Long?> = _filterGroupId

    val allGroups: StateFlow<List<LexiconGroup>> = repository.allGroups
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val items: StateFlow<List<Item>> = combine(
        repository.allItems,
        _searchQuery,
        _filterCategory,
        _filterGroupId
    ) { allItems, query, category, groupId ->
        allItems.filter { item ->
            val matchesQuery = if (query.isBlank()) true else {
                item.word.contains(query, ignoreCase = true) || item.meaning.contains(query, ignoreCase = true)
            }
            val matchesCategory = if (category == null) true else item.category == category
            val matchesGroup = if (groupId == null) true else item.groupId == groupId
            matchesQuery && matchesCategory && matchesGroup
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Initial dummy data config check
    init {
        viewModelScope.launch {
            repository.allGroups.collect { groups ->
                if (groups.isEmpty() && settings.totalRevisions == 0) {
                     // Empty DB, could insert dummy data, but let's wait.
                }
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateFilterCategory(category: String?) {
        _filterCategory.value = category
    }

    fun updateFilterGroup(groupId: Long?) {
        _filterGroupId.value = groupId
    }

    fun insertItem(item: Item) {
        viewModelScope.launch {
            repository.insertItem(item)
        }
    }

    fun updateItem(item: Item) {
        viewModelScope.launch {
            repository.updateItem(item)
        }
    }

    fun deleteItem(id: Long) {
        viewModelScope.launch {
            repository.deleteItemById(id)
        }
    }

    fun insertGroup(group: LexiconGroup) {
        viewModelScope.launch {
            repository.insertGroup(group)
        }
    }

    fun deleteGroup(id: Long) {
        viewModelScope.launch {
            repository.deleteGroup(id)
        }
    }
    
    val revisionItems: StateFlow<List<Item>> = repository.getItemsForRevision(System.currentTimeMillis())
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun processRevision(item: Item, masteryDelta: Int, easeFactorMultiplier: Double = 1.0) {
        viewModelScope.launch {
            val newMastery = (item.mastery + masteryDelta).coerceIn(0, 5)
            // SRS Schedule (days until next review):
            // Mastery 0 -> 1 day
            // Mastery 1 -> 3 days
            // Mastery 2 -> 7 days
            // Mastery 3 -> 14 days
            // Mastery 4 -> 30 days
            // Mastery 5 -> 60 days
            val baseDays = when (newMastery) {
                0 -> 1
                1 -> 3
                2 -> 7
                3 -> 14
                4 -> 30
                else -> 60
            }
            val actualDays = (baseDays * easeFactorMultiplier).toInt().coerceAtLeast(1)
            
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, actualDays)
            
            val updated = item.copy(
                mastery = newMastery,
                nextRevision = calendar.timeInMillis,
                reviewCount = item.reviewCount + 1
            )
            repository.updateItem(updated)
            settings.incrementTotalRevisions()
        }
    }
}

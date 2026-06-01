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
import android.content.Context
import android.net.Uri
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch
import java.util.Calendar

enum class SortOrder {
    DATE_ADDED_DESC,
    DATE_ADDED_ASC,
    ALPHABETICAL_A_Z,
    ALPHABETICAL_Z_A,
    MASTERY_HIGH_LOW,
    MASTERY_LOW_HIGH
}

class LexiconViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val repository = LexiconRepository(database.itemDao(), database.groupDao())
    val settings = SettingsManager(application)

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery
    
    private val _filterCategories = MutableStateFlow<Set<String>>(emptySet())
    val filterCategories: StateFlow<Set<String>> = _filterCategories

    private val _filterGroupId = MutableStateFlow<Long?>(null)
    val filterGroupId: StateFlow<Long?> = _filterGroupId

    private val _sortOrder = MutableStateFlow<SortOrder>(SortOrder.DATE_ADDED_DESC)
    val sortOrder: StateFlow<SortOrder> = _sortOrder

    private val _customCategories = MutableStateFlow(settings.categoriesString.split(",").filter { it.isNotBlank() })
    val customCategories: StateFlow<List<String>> = _customCategories

    private val _customGroupTypes = MutableStateFlow(settings.groupTypesString.split(",").filter { it.isNotBlank() })
    val customGroupTypes: StateFlow<List<String>> = _customGroupTypes

    private val _customLanguages = MutableStateFlow(settings.languagesString.split(",").filter { it.isNotBlank() })
    val customLanguages: StateFlow<List<String>> = _customLanguages

    private val _showAddDialog = MutableStateFlow(false)
    val showAddDialog: StateFlow<Boolean> = _showAddDialog

    fun setShowAddDialog(show: Boolean) {
        _showAddDialog.value = show
    }

    private val _showBottomNav = MutableStateFlow(settings.showBottomNav)
    val showBottomNav: StateFlow<Boolean> = _showBottomNav

    private val _activeTheme = MutableStateFlow(settings.activeTheme)
    val activeTheme: StateFlow<String> = _activeTheme

    private val _activeFont = MutableStateFlow(settings.activeFont)
    val activeFont: StateFlow<String> = _activeFont

    private val _activeAppLanguage = MutableStateFlow(settings.activeAppLanguage)
    val activeAppLanguage: StateFlow<String> = _activeAppLanguage

    private val _darkMode = MutableStateFlow(settings.darkMode)
    val darkMode: StateFlow<String> = _darkMode

    val allGroups: StateFlow<List<LexiconGroup>> = repository.allGroups
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val items: StateFlow<List<Item>> = combine(
        repository.allItems,
        _searchQuery,
        _filterCategories,
        _filterGroupId,
        _sortOrder
    ) { allItems, query, categories, groupId, sortOrder ->
        val filtered = allItems.filter { item ->
            val matchesQuery = if (query.isBlank()) true else {
                item.word.contains(query, ignoreCase = true) || item.meaning.contains(query, ignoreCase = true)
            }
            val matchesCategory = if (categories.isEmpty()) true else categories.contains(item.category)
            val matchesGroup = if (groupId == null) true else item.groupId == groupId
            matchesQuery && matchesCategory && matchesGroup
        }
        
        when (sortOrder) {
            SortOrder.DATE_ADDED_DESC -> filtered.sortedByDescending { it.createdAt }
            SortOrder.DATE_ADDED_ASC -> filtered.sortedBy { it.createdAt }
            SortOrder.ALPHABETICAL_A_Z -> filtered.sortedBy { it.word.lowercase() }
            SortOrder.ALPHABETICAL_Z_A -> filtered.sortedByDescending { it.word.lowercase() }
            SortOrder.MASTERY_HIGH_LOW -> filtered.sortedByDescending { it.mastery }
            SortOrder.MASTERY_LOW_HIGH -> filtered.sortedBy { it.mastery }
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

    fun toggleFilterCategory(category: String) {
        val current = _filterCategories.value.toMutableSet()
        if (current.contains(category)) {
            current.remove(category)
        } else {
            current.add(category)
        }
        _filterCategories.value = current
    }

    fun clearCategoryFilters() {
        _filterCategories.value = emptySet()
    }

    fun updateSortOrder(order: SortOrder) {
        _sortOrder.value = order
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
    
    fun updateCategories(newList: List<String>) {
        _customCategories.value = newList
        settings.categoriesString = newList.joinToString(",")
    }

    fun updateGroupTypes(newList: List<String>) {
        _customGroupTypes.value = newList
        settings.groupTypesString = newList.joinToString(",")
    }

    fun updateLanguages(newList: List<String>) {
        _customLanguages.value = newList
        settings.languagesString = newList.joinToString(",")
    }

    private val _dailyReminder = MutableStateFlow(settings.dailyReminder)
    val dailyReminder: StateFlow<Boolean> = _dailyReminder

    fun updateDailyReminder(enabled: Boolean) {
        settings.dailyReminder = enabled
        _dailyReminder.value = enabled
    }

    fun updateShowBottomNav(show: Boolean) {
        _showBottomNav.value = show
        settings.showBottomNav = show
    }

    fun updateActiveTheme(theme: String) {
        _activeTheme.value = theme
        settings.activeTheme = theme
    }

    fun updateActiveFont(font: String) {
        _activeFont.value = font
        settings.activeFont = font
    }

    fun updateActiveAppLanguage(lang: String) {
        _activeAppLanguage.value = lang
        settings.activeAppLanguage = lang
    }

    fun updateDarkMode(mode: String) {
        _darkMode.value = mode
        settings.darkMode = mode
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

    fun importData(context: Context, uri: Uri, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                if (inputStream == null) {
                    withContext(Dispatchers.Main) { onResult(false, "Could not open file") }
                    return@launch
                }
                
                val content = BufferedReader(InputStreamReader(inputStream)).use { it.readText() }
                var importedCount = 0
                
                if (content.trim().startsWith("[")) {
                    // JSON parsing
                    val jsonArray = JSONArray(content)
                    for (i in 0 until jsonArray.length()) {
                        val obj = jsonArray.optJSONObject(i) ?: continue
                        val word = obj.optString("word", "").trim()
                        val meaning = obj.optString("meaning", "").trim()
                        if (word.isNotEmpty() && meaning.isNotEmpty()) {
                            val category = obj.optString("category", "Uncategorized").takeIf { it.isNotBlank() } ?: "Uncategorized"
                            val language = obj.optString("language", "").takeIf { it.isNotBlank() && it != "null" }
                            val source = obj.optString("source", "").takeIf { it.isNotBlank() && it != "null" }
                            val note = obj.optString("note", "").takeIf { it.isNotBlank() && it != "null" }
                            
                            val item = Item(
                                word = word,
                                meaning = meaning,
                                category = category,
                                language = language,
                                source = source,
                                note = note
                            )
                            repository.insertItem(item)
                            importedCount++
                        }
                    }
                } else {
                    // CSV parsing
                    val lines = content.lines()
                    if (lines.isNotEmpty()) {
                        val header = lines.first().split(",").map { it.trim().lowercase() }
                        val wordIdx = header.indexOf("word")
                        val meaningIdx = header.indexOf("meaning")
                        val catIdx = header.indexOf("category")
                        
                        if (wordIdx != -1 && meaningIdx != -1) {
                            for (i in 1 until lines.size) {
                                val line = lines[i]
                                if (line.isBlank()) continue
                                
                                // Split by comma outside quotes
                                val cols = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)".toRegex()).map { it.trim().removeSurrounding("\"") }
                                
                                val word = cols.getOrNull(wordIdx) ?: ""
                                val meaning = cols.getOrNull(meaningIdx) ?: ""
                                
                                if (word.isNotEmpty() && meaning.isNotEmpty()) {
                                    val cat = if (catIdx != -1) cols.getOrNull(catIdx)?.takeIf { it.isNotBlank() } ?: "Uncategorized" else "Uncategorized"
                                    
                                    val item = Item(
                                        word = word,
                                        meaning = meaning,
                                        category = cat
                                    )
                                    repository.insertItem(item)
                                    importedCount++
                                }
                            }
                        } else {
                            withContext(Dispatchers.Main) { onResult(false, "CSV must contain 'word' and 'meaning' headers") }
                            return@launch
                        }
                    }
                }
                
                withContext(Dispatchers.Main) {
                    if (importedCount > 0) {
                        onResult(true, "Successfully imported $importedCount items")
                    } else {
                        onResult(false, "No valid items found to import")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { onResult(false, "Error: ${e.message}") }
            }
        }
    }
}

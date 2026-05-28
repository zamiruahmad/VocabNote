package com.example.data

import kotlinx.coroutines.flow.Flow

class LexiconRepository(
    private val itemDao: ItemDao,
    private val groupDao: LexiconGroupDao
) {
    val allItems: Flow<List<Item>> = itemDao.getAllItems()
    val allGroups: Flow<List<LexiconGroup>> = groupDao.getAllGroups()

    fun getItemsForRevision(currentTime: Long): Flow<List<Item>> {
        return itemDao.getItemsForRevision(currentTime)
    }

    suspend fun getItemById(id: Long): Item? {
        return itemDao.getItemById(id)
    }

    suspend fun insertItem(item: Item) {
        itemDao.insertItem(item)
    }

    suspend fun updateItem(item: Item) {
        itemDao.updateItem(item)
    }

    suspend fun deleteItemById(id: Long) {
        itemDao.deleteItemById(id)
    }
    
    suspend fun getRandomItems(limit: Int): List<Item> {
        return itemDao.getRandomItems(limit)
    }

    suspend fun insertGroup(group: LexiconGroup) {
        groupDao.insertGroup(group)
    }

    suspend fun deleteGroup(id: Long) {
        groupDao.clearGroupFromItems(id)
        groupDao.deleteGroupById(id)
    }
}

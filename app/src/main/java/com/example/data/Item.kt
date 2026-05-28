package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "items")
data class Item(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val category: String,
    val word: String,
    val meaning: String,
    val language: String? = null,
    val source: String? = null,
    val note: String? = null,
    val link: String? = null,
    val imageUrl: String? = null,
    val groupId: Long? = null,
    val mastery: Int = 0,
    val nextRevision: Long = System.currentTimeMillis(),
    val reviewCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

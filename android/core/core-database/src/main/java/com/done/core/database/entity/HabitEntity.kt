package com.done.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val icon: String,
    val color: String,
    val checksPerDay: Int,
    val createdAt: Long,
    val isArchived: Boolean = false,
    val sortOrder: Int = 0
)

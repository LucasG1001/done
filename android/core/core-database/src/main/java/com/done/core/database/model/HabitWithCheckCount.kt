package com.done.core.database.model

data class HabitWithCheckCount(
    val id: Long,
    val name: String,
    val icon: String,
    val color: String,
    val checksPerDay: Int,
    val createdAt: Long,
    val isArchived: Boolean,
    val sortOrder: Int,
    val checksToday: Int
)

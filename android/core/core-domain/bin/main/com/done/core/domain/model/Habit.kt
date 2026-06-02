package com.done.core.domain.model

data class Habit(
    val id: Long = 0,
    val name: String,
    val icon: String,
    val color: String,
    val checksPerDay: Int,
    val createdAt: Long,
    val isArchived: Boolean = false,
    val sortOrder: Int = 0
)

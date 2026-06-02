package com.done.core.domain.model

data class HabitStats(
    val habit: Habit,
    val currentStreak: Int,
    val bestStreak: Int,
    val completionRateLast30Days: Float,
    val completionRateLast90Days: Float,
    val totalChecks: Int,
    val completedDays: Int
)

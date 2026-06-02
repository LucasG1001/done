package com.done.core.domain.model

data class HabitWithProgress(
    val habit: Habit,
    val checksToday: Int,
    val isCompletedToday: Boolean,
    val currentStreak: Int,
    val bestStreak: Int
) {
    val progress: Float
        get() = if (habit.checksPerDay > 0) {
            (checksToday.toFloat() / habit.checksPerDay).coerceIn(0f, 1f)
        } else 0f
}

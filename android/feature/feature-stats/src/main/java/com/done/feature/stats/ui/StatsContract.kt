package com.done.feature.stats.ui

import com.done.core.domain.model.HabitStats

data class StatsUiState(
    val todayCompletion: Float = 0f,
    val weekCompletion: Float = 0f,
    val monthCompletion: Float = 0f,
    val bestStreakHabit: HabitHighlight? = null,
    val mostConsistentHabit: HabitHighlight? = null,
    val allStats: List<HabitStats> = emptyList(),
    val isLoading: Boolean = true
)

data class HabitHighlight(
    val name: String,
    val icon: String,
    val value: String,
    val label: String
)

sealed interface StatsUiAction

sealed interface StatsUiEvent

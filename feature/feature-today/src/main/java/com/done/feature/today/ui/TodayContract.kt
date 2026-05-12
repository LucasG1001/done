package com.done.feature.today.ui

import com.done.core.domain.model.HabitWithProgress

data class TodayUiState(
    val habits: List<HabitWithProgress> = emptyList(),
    val overallProgress: Float = 0f,
    val completedCount: Int = 0,
    val averageStreak: Int = 0,
    val bestRecord: Int = 0,
    val isLoading: Boolean = true,
    val error: String? = null
)

sealed interface TodayUiAction {
    data class CheckHabit(val habitId: Long) : TodayUiAction
    data class UndoCheck(val habitId: Long) : TodayUiAction
    data class OpenDetail(val habitId: Long) : TodayUiAction
}

sealed interface TodayUiEvent {
    data class NavigateToDetail(val habitId: Long) : TodayUiEvent
    data object ShowUndoSnackbar : TodayUiEvent
    data class ShowError(val message: String) : TodayUiEvent
}

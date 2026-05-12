package com.done.feature.detail.ui

import com.done.core.domain.model.DayProgress
import com.done.core.domain.model.Habit

data class DetailUiState(
    val habit: Habit? = null,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val completionRate30Days: Float = 0f,
    val totalChecks: Int = 0,
    val recentDays: List<DayProgress> = emptyList(),
    val contributionDays: List<ContributionDayUi> = emptyList(),
    val isLoading: Boolean = true
)

data class ContributionDayUi(
    val date: String,
    val checkCount: Int,
    val checksPerDay: Int
)

sealed interface DetailUiAction {
    data object Edit : DetailUiAction
    data object Archive : DetailUiAction
}

sealed interface DetailUiEvent {
    data class NavigateToEdit(val habitId: Long) : DetailUiEvent
    data object Archived : DetailUiEvent
}

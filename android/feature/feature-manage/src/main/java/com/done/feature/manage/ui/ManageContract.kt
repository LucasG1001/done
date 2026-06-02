package com.done.feature.manage.ui

import com.done.core.domain.model.Habit

data class ManageUiState(
    val name: String = "",
    val icon: String = "💪",
    val color: String = "#1D9E75",
    val checksPerDay: Int = 1,
    val isEditing: Boolean = false,
    val existingHabit: Habit? = null,
    val nameError: String? = null,
    val isSaving: Boolean = false
)

sealed interface ManageUiAction {
    data class UpdateName(val name: String) : ManageUiAction
    data class SelectIcon(val icon: String) : ManageUiAction
    data class SelectColor(val color: String) : ManageUiAction
    data class UpdateChecksPerDay(val count: Int) : ManageUiAction
    data object Save : ManageUiAction
}

sealed interface ManageUiEvent {
    data object SavedSuccessfully : ManageUiEvent
    data class ShowError(val message: String) : ManageUiEvent
}

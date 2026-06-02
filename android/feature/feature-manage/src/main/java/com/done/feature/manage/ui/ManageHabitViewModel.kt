package com.done.feature.manage.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.done.core.domain.repository.HabitRepository
import com.done.core.domain.usecase.CreateHabitUseCase
import com.done.core.domain.usecase.UpdateHabitUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ManageHabitViewModel @Inject constructor(
    private val createHabitUseCase: CreateHabitUseCase,
    private val updateHabitUseCase: UpdateHabitUseCase,
    private val habitRepository: HabitRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val habitId: Long? = savedStateHandle.get<Long>("habitId")
        ?.takeIf { it > 0 }

    private val _state = MutableStateFlow(ManageUiState())
    val state: StateFlow<ManageUiState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<ManageUiEvent>()
    val events: SharedFlow<ManageUiEvent> = _events.asSharedFlow()

    init {
        habitId?.let { loadHabit(it) }
    }

    private fun loadHabit(id: Long) {
        viewModelScope.launch {
            val habit = habitRepository.getHabitById(id) ?: return@launch
            _state.update {
                it.copy(
                    name = habit.name,
                    icon = habit.icon,
                    color = habit.color,
                    checksPerDay = habit.checksPerDay,
                    isEditing = true,
                    existingHabit = habit
                )
            }
        }
    }

    fun onAction(action: ManageUiAction) {
        when (action) {
            is ManageUiAction.UpdateName -> {
                _state.update { it.copy(name = action.name, nameError = null) }
            }
            is ManageUiAction.SelectIcon -> {
                _state.update { it.copy(icon = action.icon) }
            }
            is ManageUiAction.SelectColor -> {
                _state.update { it.copy(color = action.color) }
            }
            is ManageUiAction.UpdateChecksPerDay -> {
                _state.update { it.copy(checksPerDay = action.count.coerceIn(1, 20)) }
            }
            ManageUiAction.Save -> save()
        }
    }

    private fun save() {
        val current = _state.value
        if (current.isSaving) return

        _state.update { it.copy(isSaving = true) }

        viewModelScope.launch {
            val result = if (current.isEditing && habitId != null) {
                when (val r = updateHabitUseCase(
                    habitId, current.name, current.icon, current.color, current.checksPerDay
                )) {
                    is UpdateHabitUseCase.Result.Success -> null
                    is UpdateHabitUseCase.Result.ValidationError -> r.message
                    is UpdateHabitUseCase.Result.NotFound -> "Hábito não encontrado"
                }
            } else {
                when (val r = createHabitUseCase(
                    current.name, current.icon, current.color, current.checksPerDay
                )) {
                    is CreateHabitUseCase.Result.Success -> null
                    is CreateHabitUseCase.Result.ValidationError -> r.message
                }
            }

            if (result != null) {
                _state.update { it.copy(nameError = result, isSaving = false) }
            } else {
                _state.update { it.copy(isSaving = false) }
                _events.emit(ManageUiEvent.SavedSuccessfully)
            }
        }
    }
}

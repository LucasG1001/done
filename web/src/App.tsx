import { useState } from 'react'
import type { Habit, HabitFormData } from './types/habit'
import { useHabits } from './hooks/useHabits'
import { Header } from './components/Header/Header'
import { HabitCard } from './components/HabitCard/HabitCard'
import { SidePanel } from './components/SidePanel/SidePanel'
import { HabitForm } from './components/HabitForm/HabitForm'
import styles from './App.module.css'

interface FormState {
  isOpen: boolean
  mode: 'create' | 'edit'
  editingHabit: Habit | null
}

export function App() {
  const { habits, createHabit, updateHabit, deleteHabit, toggleCompletion } = useHabits()
  const [selectedHabit, setSelectedHabit] = useState<Habit | null>(null)
  const [formState, setFormState] = useState<FormState>({
    isOpen: false,
    mode: 'create',
    editingHabit: null,
  })

  function handleOpenCreate() {
    setFormState({ isOpen: true, mode: 'create', editingHabit: null })
  }

  function handleOpenEdit(habit: Habit) {
    setSelectedHabit(null)
    setFormState({ isOpen: true, mode: 'edit', editingHabit: habit })
  }

  function handleCloseForm() {
    setFormState({ isOpen: false, mode: 'create', editingHabit: null })
  }

  function handleSaveForm(data: HabitFormData) {
    if (formState.mode === 'edit' && formState.editingHabit) {
      updateHabit(formState.editingHabit.id, data)
    } else {
      createHabit(data)
    }
    handleCloseForm()
  }

  function handleDeleteHabit(id: string) {
    deleteHabit(id)
    setSelectedHabit(null)
  }

  function handleSelectHabit(habit: Habit) {
    setSelectedHabit(habit)
  }

  function handleClosePanel() {
    setSelectedHabit(null)
  }

  const activePanelHabit = selectedHabit
    ? habits.find((h) => h.id === selectedHabit.id) ?? null
    : null

  return (
    <div className={styles.app}>
      <Header />

      {habits.length === 0 ? (
        <div className={styles.emptyState}>
          <span className={styles.emptyIcon}>🌱</span>
          <h2 className={styles.emptyTitle}>Nenhum hábito ainda</h2>
          <p className={styles.emptyText}>
            Toque no botão + para criar seu primeiro hábito
          </p>
        </div>
      ) : (
        <div className={styles.list}>
          {habits.map((habit) => (
            <HabitCard
              key={habit.id}
              habit={habit}
              onToggle={toggleCompletion}
              onSelect={handleSelectHabit}
            />
          ))}
        </div>
      )}

      <button
        className={styles.fab}
        onClick={handleOpenCreate}
        aria-label="Criar novo hábito"
      >
        +
      </button>

      {activePanelHabit && (
        <SidePanel
          habit={activePanelHabit}
          onClose={handleClosePanel}
          onEdit={handleOpenEdit}
          onDelete={handleDeleteHabit}
        />
      )}

      {formState.isOpen && (
        <HabitForm
          mode={formState.mode}
          initialData={
            formState.editingHabit
              ? {
                  name: formState.editingHabit.name,
                  selectedDays: formState.editingHabit.selectedDays,
                }
              : undefined
          }
          onSave={handleSaveForm}
          onClose={handleCloseForm}
        />
      )}
    </div>
  )
}

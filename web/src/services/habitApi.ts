import type { Habit, HabitFormData } from '../types/habit'

const API_URL = import.meta.env.VITE_API_URL ?? 'http://localhost:3333'

export async function fetchHabits(): Promise<Habit[]> {
  const response = await fetch(`${API_URL}/habits`)
  if (!response.ok) throw new Error('Erro ao carregar hábitos')
  return response.json() as Promise<Habit[]>
}

export async function createHabit(data: HabitFormData): Promise<Habit> {
  const response = await fetch(`${API_URL}/habits`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data),
  })
  if (!response.ok) throw new Error('Erro ao criar hábito')
  return response.json() as Promise<Habit>
}

export async function updateHabit(id: string, data: HabitFormData): Promise<Habit> {
  const response = await fetch(`${API_URL}/habits/${id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data),
  })
  if (!response.ok) throw new Error('Erro ao atualizar hábito')
  return response.json() as Promise<Habit>
}

export async function deleteHabit(id: string): Promise<void> {
  const response = await fetch(`${API_URL}/habits/${id}`, {
    method: 'DELETE',
  })
  if (!response.ok) throw new Error('Erro ao excluir hábito')
}

export async function toggleHabitCompletion(habitId: string, date: string): Promise<Habit> {
  const response = await fetch(`${API_URL}/habits/${habitId}/toggle/${date}`, {
    method: 'PATCH',
  })
  if (!response.ok) throw new Error('Erro ao atualizar conclusão')
  return response.json() as Promise<Habit>
}

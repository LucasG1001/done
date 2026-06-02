import { Router, type Request, type Response } from 'express'
import { pool } from '../db.js'

export const habitsRouter = Router()

interface HabitRow {
  id: string
  name: string
  selected_days: number[]
  current_streak: number
  longest_streak: number
  level: number
  created_at: string
  updated_at: string
}

interface CompletionRow {
  habit_id: string
  date: string
  completed: boolean
}

interface HabitResponse {
  id: string
  name: string
  selectedDays: number[]
  completions: { date: string; completed: boolean }[]
  currentStreak: number
  longestStreak: number
  level: number
  createdAt: string
  updatedAt: string
}

function toHabitResponse(habit: HabitRow, completions: CompletionRow[]): HabitResponse {
  return {
    id: habit.id,
    name: habit.name,
    selectedDays: habit.selected_days,
    completions: completions
      .filter((c) => c.habit_id === habit.id)
      .map((c) => ({ date: c.date, completed: c.completed })),
    currentStreak: habit.current_streak,
    longestStreak: habit.longest_streak,
    level: habit.level,
    createdAt: habit.created_at,
    updatedAt: habit.updated_at,
  }
}

habitsRouter.get('/', async (_req: Request, res: Response) => {
  const habitsResult = await pool.query<HabitRow>(
    'SELECT * FROM habits ORDER BY created_at ASC'
  )
  const completionsResult = await pool.query<CompletionRow>(
    'SELECT habit_id, date, completed FROM habit_completions'
  )

  const habits = habitsResult.rows.map((habit) =>
    toHabitResponse(habit, completionsResult.rows)
  )

  res.json(habits)
})

habitsRouter.post('/', async (req: Request, res: Response) => {
  const { name, selectedDays } = req.body as { name: string; selectedDays: number[] }

  const result = await pool.query<HabitRow>(
    `INSERT INTO habits (name, selected_days)
     VALUES ($1, $2)
     RETURNING *`,
    [name, selectedDays]
  )

  res.status(201).json(toHabitResponse(result.rows[0]!, []))
})

habitsRouter.put('/:id', async (req: Request, res: Response) => {
  const { id } = req.params
  const { name, selectedDays } = req.body as { name: string; selectedDays: number[] }

  const result = await pool.query<HabitRow>(
    `UPDATE habits
     SET name = $1, selected_days = $2, updated_at = NOW()
     WHERE id = $3
     RETURNING *`,
    [name, selectedDays, id]
  )

  if (result.rowCount === 0) {
    res.status(404).json({ error: 'Hábito não encontrado' })
    return
  }

  const completionsResult = await pool.query<CompletionRow>(
    'SELECT habit_id, date, completed FROM habit_completions WHERE habit_id = $1',
    [id]
  )

  res.json(toHabitResponse(result.rows[0]!, completionsResult.rows))
})

habitsRouter.delete('/:id', async (req: Request, res: Response) => {
  const { id } = req.params

  const result = await pool.query('DELETE FROM habits WHERE id = $1', [id])

  if (result.rowCount === 0) {
    res.status(404).json({ error: 'Hábito não encontrado' })
    return
  }

  res.status(204).send()
})

habitsRouter.patch('/:id/toggle/:date', async (req: Request, res: Response) => {
  const { id, date } = req.params

  const existing = await pool.query<CompletionRow>(
    'SELECT * FROM habit_completions WHERE habit_id = $1 AND date = $2',
    [id, date]
  )

  if (existing.rowCount === 0) {
    await pool.query(
      `INSERT INTO habit_completions (habit_id, date, completed)
       VALUES ($1, $2, TRUE)`,
      [id, date]
    )
  } else {
    await pool.query(
      `UPDATE habit_completions
       SET completed = NOT completed
       WHERE habit_id = $1 AND date = $2`,
      [id, date]
    )
  }

  await pool.query('UPDATE habits SET updated_at = NOW() WHERE id = $1', [id])

  const habitResult = await pool.query<HabitRow>('SELECT * FROM habits WHERE id = $1', [id])
  const completionsResult = await pool.query<CompletionRow>(
    'SELECT habit_id, date, completed FROM habit_completions WHERE habit_id = $1',
    [id]
  )

  if (habitResult.rowCount === 0) {
    res.status(404).json({ error: 'Hábito não encontrado' })
    return
  }

  res.json(toHabitResponse(habitResult.rows[0]!, completionsResult.rows))
})

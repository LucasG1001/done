import { Router, type Request, type Response } from 'express'
import { pool } from '../db.js'
import { isValidTime } from '../utils/datetime.js'
import {
  clearCompletion,
  CompletionLockedError,
  getCompletion,
  setCompletion,
} from '../services/completions.js'

export const habitsRouter = Router()

interface HabitRow {
  id: string
  name: string
  selected_days: number[]
  scheduled_time: string | null
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
  locked: boolean
}

interface HabitResponse {
  id: string
  name: string
  selectedDays: number[]
  scheduledTime: string | null
  completions: { date: string; completed: boolean; locked: boolean }[]
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
    scheduledTime: habit.scheduled_time,
    completions: completions
      .filter((c) => c.habit_id === habit.id)
      .map((c) => ({ date: c.date, completed: c.completed, locked: c.locked })),
    currentStreak: habit.current_streak,
    longestStreak: habit.longest_streak,
    level: habit.level,
    createdAt: habit.created_at,
    updatedAt: habit.updated_at,
  }
}

function normalizeScheduledTime(value: unknown): string | null {
  if (value === null || value === undefined || value === '') return null
  if (typeof value === 'string' && isValidTime(value)) return value
  return undefined as unknown as null
}

habitsRouter.get('/', async (_req: Request, res: Response) => {
  const habitsResult = await pool.query<HabitRow>(
    'SELECT * FROM habits ORDER BY created_at ASC'
  )
  const completionsResult = await pool.query<CompletionRow>(
    'SELECT habit_id, date, completed, locked FROM habit_completions'
  )

  const habits = habitsResult.rows.map((habit) =>
    toHabitResponse(habit, completionsResult.rows)
  )

  res.json(habits)
})

habitsRouter.post('/', async (req: Request, res: Response) => {
  const { name, selectedDays, scheduledTime } = req.body as {
    name: string
    selectedDays: number[]
    scheduledTime?: string | null
  }

  const normalizedTime = normalizeScheduledTime(scheduledTime)
  if (normalizedTime === undefined) {
    res.status(400).json({ error: 'Horário inválido' })
    return
  }

  const result = await pool.query<HabitRow>(
    `INSERT INTO habits (name, selected_days, scheduled_time)
     VALUES ($1, $2, $3)
     RETURNING *`,
    [name, selectedDays, normalizedTime]
  )

  res.status(201).json(toHabitResponse(result.rows[0]!, []))
})

habitsRouter.put('/:id', async (req: Request, res: Response) => {
  const { id } = req.params
  const { name, selectedDays, scheduledTime } = req.body as {
    name: string
    selectedDays: number[]
    scheduledTime?: string | null
  }

  const normalizedTime = normalizeScheduledTime(scheduledTime)
  if (normalizedTime === undefined) {
    res.status(400).json({ error: 'Horário inválido' })
    return
  }

  const result = await pool.query<HabitRow>(
    `UPDATE habits
     SET name = $1, selected_days = $2, scheduled_time = $3, updated_at = NOW()
     WHERE id = $4
     RETURNING *`,
    [name, selectedDays, normalizedTime, id]
  )

  if (result.rowCount === 0) {
    res.status(404).json({ error: 'Hábito não encontrado' })
    return
  }

  const completionsResult = await pool.query<CompletionRow>(
    'SELECT habit_id, date, completed, locked FROM habit_completions WHERE habit_id = $1',
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

async function respondWithHabit(id: string, res: Response): Promise<void> {
  const habitResult = await pool.query<HabitRow>('SELECT * FROM habits WHERE id = $1', [id])
  if (habitResult.rowCount === 0) {
    res.status(404).json({ error: 'Hábito não encontrado' })
    return
  }

  const completionsResult = await pool.query<CompletionRow>(
    'SELECT habit_id, date, completed, locked FROM habit_completions WHERE habit_id = $1',
    [id]
  )

  res.json(toHabitResponse(habitResult.rows[0]!, completionsResult.rows))
}

habitsRouter.patch('/:id/toggle/:date', async (req: Request, res: Response) => {
  const { id, date } = req.params

  const existing = await getCompletion(id!, date!)

  try {
    if (!existing) {
      await setCompletion(id!, date!, true)
    } else if (existing.completed) {
      await setCompletion(id!, date!, false)
    } else {
      await clearCompletion(id!, date!)
    }
  } catch (error) {
    if (error instanceof CompletionLockedError) {
      res.status(409).json({ error: error.message })
      return
    }
    throw error
  }

  await respondWithHabit(id!, res)
})

habitsRouter.patch('/:id/completion/:date', async (req: Request, res: Response) => {
  const { id, date } = req.params
  const { status } = req.body as { status: 'done' | 'notDone' | 'clear' }

  try {
    if (status === 'done') {
      await setCompletion(id!, date!, true)
    } else if (status === 'notDone') {
      await setCompletion(id!, date!, false)
    } else if (status === 'clear') {
      await clearCompletion(id!, date!)
    } else {
      res.status(400).json({ error: 'Status inválido' })
      return
    }
  } catch (error) {
    if (error instanceof CompletionLockedError) {
      res.status(409).json({ error: error.message })
      return
    }
    throw error
  }

  await respondWithHabit(id!, res)
})

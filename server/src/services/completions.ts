import { pool } from '../db.js'

export interface CompletionState {
  date: string
  completed: boolean
  locked: boolean
}

export class CompletionLockedError extends Error {
  constructor() {
    super('Este registro foi marcado automaticamente e não pode ser desfeito')
    this.name = 'CompletionLockedError'
  }
}

export async function getCompletion(
  habitId: string,
  date: string
): Promise<CompletionState | null> {
  const result = await pool.query<CompletionState>(
    'SELECT date, completed, locked FROM habit_completions WHERE habit_id = $1 AND date = $2',
    [habitId, date]
  )
  return result.rows[0] ?? null
}

export async function setCompletion(
  habitId: string,
  date: string,
  completed: boolean,
  options: { locked?: boolean; force?: boolean } = {}
): Promise<void> {
  const existing = await getCompletion(habitId, date)

  if (existing?.locked && !options.force) {
    throw new CompletionLockedError()
  }

  const locked = options.locked ?? existing?.locked ?? false

  if (existing) {
    await pool.query(
      'UPDATE habit_completions SET completed = $1, locked = $2 WHERE habit_id = $3 AND date = $4',
      [completed, locked, habitId, date]
    )
  } else {
    await pool.query(
      'INSERT INTO habit_completions (habit_id, date, completed, locked) VALUES ($1, $2, $3, $4)',
      [habitId, date, completed, locked]
    )
  }

  await pool.query('UPDATE habits SET updated_at = NOW() WHERE id = $1', [habitId])
}

export async function clearCompletion(habitId: string, date: string): Promise<void> {
  const existing = await getCompletion(habitId, date)

  if (existing?.locked) {
    throw new CompletionLockedError()
  }

  await pool.query('DELETE FROM habit_completions WHERE habit_id = $1 AND date = $2', [
    habitId,
    date,
  ])
  await pool.query('UPDATE habits SET updated_at = NOW() WHERE id = $1', [habitId])
}

import { pool } from './db.js'

export async function migrate(): Promise<void> {
  await pool.query(`
    CREATE TABLE IF NOT EXISTS habits (
      id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
      name TEXT NOT NULL,
      selected_days INTEGER[] NOT NULL,
      scheduled_time TEXT,
      current_streak INTEGER NOT NULL DEFAULT 0,
      longest_streak INTEGER NOT NULL DEFAULT 0,
      level INTEGER NOT NULL DEFAULT 1,
      created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
      updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
    );

    CREATE TABLE IF NOT EXISTS habit_completions (
      id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
      habit_id UUID NOT NULL REFERENCES habits(id) ON DELETE CASCADE,
      date TEXT NOT NULL,
      completed BOOLEAN NOT NULL DEFAULT TRUE,
      locked BOOLEAN NOT NULL DEFAULT FALSE,
      UNIQUE(habit_id, date)
    );

    CREATE TABLE IF NOT EXISTS habit_reminders (
      id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
      habit_id UUID NOT NULL REFERENCES habits(id) ON DELETE CASCADE,
      date TEXT NOT NULL,
      reminder_count INTEGER NOT NULL DEFAULT 0,
      last_sent_at TIMESTAMPTZ,
      resolved BOOLEAN NOT NULL DEFAULT FALSE,
      UNIQUE(habit_id, date)
    );

    ALTER TABLE habits ADD COLUMN IF NOT EXISTS scheduled_time TEXT;
    ALTER TABLE habit_completions ADD COLUMN IF NOT EXISTS locked BOOLEAN NOT NULL DEFAULT FALSE;
  `)
}

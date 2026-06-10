import { getUpdates, isTelegramConfigured } from './telegram.js'
import { handleCallback, runReminderTick } from './reminders.js'

const TICK_INTERVAL_MS = 60 * 1000

async function pollUpdates(): Promise<void> {
  let offset = 0

  while (true) {
    const updates = await getUpdates(offset)

    for (const update of updates) {
      offset = update.update_id + 1
      if (update.callback_query) {
        try {
          await handleCallback(update.callback_query)
        } catch {
          continue
        }
      }
    }

    if (updates.length === 0) {
      await new Promise((resolve) => setTimeout(resolve, 1000))
    }
  }
}

export function startWorker(): void {
  if (!isTelegramConfigured()) {
    console.log('Telegram não configurado — worker de lembretes desativado')
    return
  }

  void runReminderTick()
  setInterval(() => {
    void runReminderTick()
  }, TICK_INTERVAL_MS)

  void pollUpdates()

  console.log('Worker de lembretes do Telegram iniciado')
}

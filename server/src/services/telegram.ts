const BOT_TOKEN = process.env.TELEGRAM_BOT_TOKEN
const CHAT_ID = process.env.TELEGRAM_CHAT_ID

const API_BASE = BOT_TOKEN ? `https://api.telegram.org/bot${BOT_TOKEN}` : ''

interface InlineButton {
  text: string
  callbackData: string
}

interface TelegramCallbackQuery {
  id: string
  data?: string
  message?: { chat: { id: number }; message_id: number }
}

interface TelegramUpdate {
  update_id: number
  callback_query?: TelegramCallbackQuery
}

export function isTelegramConfigured(): boolean {
  return Boolean(BOT_TOKEN && CHAT_ID)
}

export async function sendMessage(
  text: string,
  buttons: InlineButton[][] = []
): Promise<{ messageId: number } | null> {
  if (!isTelegramConfigured()) return null

  const replyMarkup =
    buttons.length > 0
      ? {
          inline_keyboard: buttons.map((row) =>
            row.map((button) => ({ text: button.text, callback_data: button.callbackData }))
          ),
        }
      : undefined

  try {
    const response = await fetch(`${API_BASE}/sendMessage`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        chat_id: CHAT_ID,
        text,
        parse_mode: 'HTML',
        reply_markup: replyMarkup,
      }),
    })

    const payload = (await response.json()) as { ok: boolean; result?: { message_id: number } }
    if (!payload.ok || !payload.result) return null
    return { messageId: payload.result.message_id }
  } catch {
    return null
  }
}

export async function editMessageText(messageId: number, text: string): Promise<void> {
  if (!isTelegramConfigured()) return

  try {
    await fetch(`${API_BASE}/editMessageText`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        chat_id: CHAT_ID,
        message_id: messageId,
        text,
        parse_mode: 'HTML',
      }),
    })
  } catch {
    return
  }
}

export async function answerCallbackQuery(callbackQueryId: string, text: string): Promise<void> {
  if (!isTelegramConfigured()) return

  try {
    await fetch(`${API_BASE}/answerCallbackQuery`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ callback_query_id: callbackQueryId, text }),
    })
  } catch {
    return
  }
}

export async function getUpdates(offset: number): Promise<TelegramUpdate[]> {
  if (!isTelegramConfigured()) return []

  try {
    const response = await fetch(
      `${API_BASE}/getUpdates?offset=${offset}&timeout=30&allowed_updates=["callback_query"]`
    )
    const payload = (await response.json()) as { ok: boolean; result?: TelegramUpdate[] }
    if (!payload.ok || !payload.result) return []
    return payload.result
  } catch {
    return []
  }
}

export type { TelegramUpdate, TelegramCallbackQuery }

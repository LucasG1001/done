import 'dotenv/config'
import express from 'express'
import cors from 'cors'
import { migrate } from './migrate.js'
import { habitsRouter } from './routes/habits.js'

const app = express()
const PORT = process.env.PORT ?? 3333

app.use(cors())
app.use(express.json())

app.use('/habits', habitsRouter)

await migrate()

app.listen(PORT, () => {
  console.log(`Server running on http://localhost:${PORT}`)
})

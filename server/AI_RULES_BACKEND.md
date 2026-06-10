# Padrões e Boas Práticas do Backend (Done)
*Leia este arquivo antes de sugerir ou escrever qualquer código para este projeto.*

## 0. Visão Geral do Projeto

**Done** é um habit tracker minimalista. O sistema tem três partes:
- `web/` — Frontend React + Vite + TypeScript (CSS Modules, sem bibliotecas de UI)
- `server/` — Backend Node.js + Express + PostgreSQL (este diretório)
- `android/` — Ignorado por ora

O frontend se comunica exclusivamente com o backend via API REST. Nenhum dado é persistido no cliente. Os dados mockados (`web/src/data/mockHabits.ts`) foram abandonados e existem apenas como referência.

---

## 1. Idioma e Comentários
- **Código em Inglês:** Variáveis, funções, tipos, rotas, arquivos e pastas.
- **Mensagens de erro ao usuário em Português:** Strings retornadas nos campos `error` do JSON.
- **Zero Comentários:** Código limpo e autoexplicativo. Sem comentários no código final.

---

## 2. Stack Técnica e Restrições
- **Runtime:** Node.js com TypeScript (`strict: true`). Proibido `any` e `eslint-disable`.
- **Framework:** Express 5.
- **Banco de Dados:** PostgreSQL via `pg` (pool de conexão). Nenhum ORM.
- **Dev Runner:** `tsx watch src/server.ts` para hot-reload em desenvolvimento.
- **Build de Produção:** `tsc` compila para `dist/`. Em produção roda `node dist/server.js`.
- **Variáveis de Ambiente:** Carregadas via `dotenv`. Nunca hardcoded no código.
- **Module System:** `"module": "NodeNext"` no tsconfig — imports internos usam extensão `.js` mesmo sendo `.ts` (ex: `import { pool } from '../db.js'`).

---

## 3. Convenções de Nomenclatura
- **Banco de dados:** `snake_case` para tabelas e colunas (`selected_days`, `created_at`).
- **Código TypeScript e JSON da API:** `camelCase` (`selectedDays`, `createdAt`).
- A camada de rotas (`routes/habits.ts`) é responsável por mapear entre os dois formatos via a função `toHabitResponse`.

---

## 4. Estrutura de Pastas
```
server/
  src/
    db.ts           ← pool de conexão PostgreSQL (usa DATABASE_URL do .env)
    migrate.ts      ← criação de tabelas (idempotente, IF NOT EXISTS)
    server.ts       ← entry point: roda migrate() ANTES do listen()
    routes/
      habits.ts     ← todas as rotas REST de hábitos
  .env              ← credenciais locais (não vai para o git)
  .env.example      ← template das variáveis (vai para o git) — fica na raiz do projeto
  Dockerfile        ← multi-stage: compila TS → imagem final lean
  .dockerignore
  AI_RULES_BACKEND.md
```

---

## 5. Schema do Banco de Dados
```sql
CREATE TABLE habits (
  id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  name         TEXT NOT NULL,
  selected_days INTEGER[] NOT NULL,
  current_streak INTEGER NOT NULL DEFAULT 0,
  longest_streak INTEGER NOT NULL DEFAULT 0,
  level        INTEGER NOT NULL DEFAULT 1,
  created_at   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at   TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE habit_completions (
  id       UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  habit_id UUID NOT NULL REFERENCES habits(id) ON DELETE CASCADE,
  date     TEXT NOT NULL,           -- formato: "YYYY-MM-DD"
  completed BOOLEAN NOT NULL DEFAULT TRUE,
  UNIQUE(habit_id, date)
);
```

**Observações importantes:**
- `selected_days` é `INTEGER[]` — array de dias da semana (0=Dom, 1=Seg, …, 6=Sáb).
- `date` em `habit_completions` é `TEXT` no formato `"YYYY-MM-DD"`, não `DATE`, para evitar problemas de timezone.
- `current_streak`, `longest_streak` e `level` são **calculados pelo frontend** após cada resposta da API usando as funções em `web/src/utils/`. O banco os armazena mas não os recalcula.
- `habit_completions` tem `UNIQUE(habit_id, date)` — o toggle usa INSERT ou UPDATE, nunca duplica.

---

## 6. Rotas da API

Base URL em dev: `http://localhost:3333`
Base URL em produção: `/api` (proxied pelo nginx — veja seção de Deploy)

| Método | Rota | Descrição |
|--------|------|-----------|
| GET | `/habits` | Retorna todos os hábitos com suas completions |
| POST | `/habits` | Cria um hábito (body: `{ name, selectedDays }`) |
| PUT | `/habits/:id` | Atualiza nome e dias (body: `{ name, selectedDays }`) |
| DELETE | `/habits/:id` | Exclui o hábito e suas completions (cascade) |
| PATCH | `/habits/:id/toggle/:date` | Inverte o `completed` de uma data (`date` = `YYYY-MM-DD`) |

**Padrões de resposta:**
- `201` em criações, `204` em deleções, `404` quando não encontrado.
- PUT e PATCH sempre retornam o objeto completo e atualizado.
- `GET /habits` faz duas queries: uma para `habits`, outra para `habit_completions`, e monta a resposta em memória (evita N+1).

---

## 7. Ambientes

### Desenvolvimento (local)
```bash
# Pré-requisito: SSH tunnel ativo para o PostgreSQL de testes na VPS
ssh -L 5432:localhost:5432 lucas@187.77.62.157

# Rodar o servidor
cd server && npm run dev
```
O arquivo `server/.env` aponta para `127.0.0.1:5432` (banco de testes na VPS).

### Produção (Docker na VPS)
Toda a stack roda em Docker Compose auto-suficiente — **PostgreSQL próprio**, sem depender do banco de testes.

```
VPS: 187.77.62.157  (user: lucas)

docker-compose.yml (na raiz do projeto):
  ├── postgres   → volume persistente (postgres_data)
  ├── server     → conecta em postgres:5432
  └── web/nginx  → Porta 8080 (bloqueada via UFW para a internet)
                   /api/* proxied para server:3333
```

**Segurança e Acesso Remoto (VPN):**
Para acessar o app do celular ou de outros computadores de forma segura (sem expor o web app à internet aberta), a VPS utiliza o **WireGuard VPN** e o firewall **UFW**.
- O container web expõe a porta `8080`.
- O UFW bloqueia acessos públicos (`sudo ufw deny 8080`) e libera apenas conexões da interface da VPN (`sudo ufw allow in on wg0 to any port 8080`).
- **Como Acessar:** Com o WireGuard ativado no seu dispositivo (usando o perfil de cliente gerado na VPS), basta abrir `http://187.77.62.157:8080` no navegador.
- *Nota: Não é mais necessário usar SSH Tunnel (`ssh -L`) para acessar o web app se você estiver conectado à VPN.*

Variáveis de ambiente de produção ficam em `.env` na **raiz do projeto** na VPS (nunca no git). Template em `.env.example`.

---

## 8. Deploy e CI/CD

Todo push na branch `main` dispara o GitHub Actions (`.github/workflows/deploy.yml`):
1. SSH na VPS
2. `git pull origin main`
3. `docker compose up -d --build --remove-orphans`
4. `docker image prune -f`

**GitHub Secrets necessários:**
- `VPS_HOST` = `187.77.62.157`
- `VPS_USER` = `lucas`
- `VPS_SSH_KEY` = chave privada SSH
- `VPS_APP_PATH` = `/home/lucas/done` (ou onde o repo estiver clonado)

---

## 9. Frontend — Como se Integra

O frontend (`web/`) usa:
- `web/src/services/habitApi.ts` — camada de serviço com todos os `fetch`. A URL base é `import.meta.env.VITE_API_URL ?? 'http://localhost:3333'`. Em produção, o Dockerfile do web injeta `VITE_API_URL=/api` em build time.
- `web/src/hooks/useHabits.ts` — consome `habitApi.ts`, gerencia estado com `useState`, expõe `loading` e `error`.
- Streak, nível e estatísticas são **recalculados no frontend** via `web/src/utils/streakUtils.ts` e `levelUtils.ts` após cada resposta da API.

---

## 10. Evolução destas Regras (Nota para a IA)
- **Atualização Contínua:** Se identificar um padrão importante não documentado, adicione aqui.
- **Seja Conciso:** Mantenha as adições curtas e diretas, este arquivo é sua memória do projeto.

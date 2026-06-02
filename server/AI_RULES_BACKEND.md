# Padrões e Boas Práticas do Backend (Done)
*Leia este arquivo antes de sugerir ou escrever qualquer código para o backend deste projeto.*

## 1. Idioma e Comentários
- **Código em Inglês:** Variáveis, funções, tipos, rotas, arquivos e pastas.
- **Mensagens de erro para o usuário em Português:** Strings retornadas nos campos `error` do JSON.
- **Zero Comentários:** Código limpo e autoexplicativo. Sem comentários no código final.

## 2. Stack Técnica e Restrições
- **Runtime:** Node.js com TypeScript (modo estrito `strict: true`). Proibido o uso de `any` e `eslint-disable`.
- **Framework:** Express 5.
- **Banco de Dados:** PostgreSQL via `pg` (pool de conexão). Nenhum ORM.
- **Dev Runner:** `tsx watch` para hot-reload em desenvolvimento.
- **Variáveis de Ambiente:** Carregadas via `dotenv`. Nunca hardcoded no código.

## 3. Convenções de Nomenclatura
- **Banco de dados:** `snake_case` para nomes de tabelas e colunas (`selected_days`, `created_at`).
- **Código TypeScript e JSON da API:** `camelCase` (`selectedDays`, `createdAt`).
- A camada de rotas é responsável por fazer o mapeamento entre os dois formatos.

## 4. Estrutura de Pastas
```
server/
  src/
    db.ts           ← pool de conexão PostgreSQL
    migrate.ts      ← criação de tabelas (idempotente)
    server.ts       ← entry point Express
    routes/
      habits.ts     ← rotas REST de hábitos
```

## 5. Padrões de API
- Retornar `404` quando um recurso não é encontrado.
- Retornar `201` em criações bem-sucedidas.
- Retornar `204` em deleções bem-sucedidas.
- Sempre retornar o objeto completo e atualizado após PUT/PATCH.
- O campo `completed` em `habit_completions` é `BOOLEAN` — um PATCH em `/habits/:id/toggle/:date` inverte o valor (INSERT se não existir).

## 6. Banco de Dados
- Conexão via SSH tunnel local: `127.0.0.1:5432`
- Tabelas principais: `habits`, `habit_completions`
- `habit_completions` tem constraint `UNIQUE(habit_id, date)` para evitar duplicatas.
- A migration roda automaticamente no startup (`await migrate()` antes do `listen`).

## 7. Evolução destas Regras (Nota para a IA)
- **Atualização Contínua:** Se identificar um padrão importante não documentado, adicione aqui.
- **Seja Conciso:** Mantenha as adições curtas e diretas.

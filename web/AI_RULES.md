# Padrões e Boas Práticas do Projeto (Done)
*Leia este arquivo antes de sugerir ou escrever qualquer código para este projeto.*

## 1. Idioma e Comentários
- **Código em Inglês:** Variáveis, funções, tipos, interfaces, arquivos, pastas, hooks, utils, constantes e classes CSS.
- **Texto Visível em Português:** Labels, placeholders, mensagens, títulos e qualquer texto renderizado para o usuário.
- **Zero Comentários:** O código deve ser limpo e autoexplicativo através de nomes significativos (Clean Code). Não adicione comentários no código final.

## 2. Stack Técnica e Restrições
- **React 18+ com TypeScript:** Modo estrito ativado (`strict: true`). Proibido o uso de `any` e `eslint-disable`.
- **Estilização:** Apenas CSS Modules Vanilla (`.module.css`). Nenhuma biblioteca de UI externa (Tailwind, MUI, etc).
- **Gerenciamento de Estado:** Apenas ferramentas nativas do React (`useState`, `useReducer`, `useContext`). Nenhum Redux, Zustand ou afins.
- **Roteamento:** Apenas navegação via estado interno do React. Nenhuma lib de roteamento (React Router, etc).
- **Build Tool:** Vite.

## 3. Padrões de Código e Arquitetura
- **Clean Code:** Funções pequenas, focadas e com responsabilidade única.
- **Estrutura de Componentes:** Cada componente deve ter sua própria pasta contendo o `.tsx` e o `.module.css`.
- **Separação de Responsabilidades:**
  - Lógica de domínio e cálculos isolados em funções puras na pasta `utils/` (ex: `dateUtils.ts`, `streakUtils.ts`).
  - Lógica de estado e integração isolada em Custom Hooks na pasta `hooks/` (ex: `useHabits.ts`).
  - Definições de TypeScript centralizadas na pasta `types/`.

## 4. UI, UX e Acessibilidade (A11y)
- **Acessibilidade:** Todos os elementos interativos (botões, toggles, cards) devem ser acessíveis via teclado (usar `tabIndex`, `onKeyDown` para `Enter` e `Espaço`).
- **Nomenclatura CSS:** Utilizar `camelCase` para nomes de classes nos arquivos `.module.css` (para facilitar a importação no TSX).
- **Design System:** Basear-se em variáveis CSS globais definidas no `global.css` (cores, espaçamentos, transições e tipografia).
- **Console Limpo:** Nenhum `console.log` deve ser deixado no código final.

## 5. Evolução destas Regras (Nota para a IA)
- **Atualização Contínua:** Se você (a IA) identificar um novo padrão importante, restrição não documentada ou regra útil durante o desenvolvimento, **adicione a este arquivo**.
- **Seja Conciso:** Mantenha as adições o mais curtas e diretas possível, apenas com o estritamente necessário, para que este documento não fique poluído ou longo demais, esse arquivo basicamente e sua memoria oque você achar importante você lembrar em um futuro deve estar aqui.

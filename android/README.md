# Done — Habit Tracker

Aplicativo Android nativo de rastreamento de hábitos, totalmente offline, com visual moderno usando Jetpack Compose e Material 3.

## 🏗️ Arquitetura

O projeto segue **Clean Architecture** com padrão **MVI** (Model-View-Intent) e modularização por feature.

```
Done/
├── app/                     → Módulo principal, DI root, MainActivity, Navegação
├── core/
│   ├── core-database/       → Room, DAOs, entidades, repositórios
│   ├── core-domain/         → Modelos de domínio, use cases, interfaces de repositório
│   ├── core-ui/             → Componentes Compose compartilhados, tema, tokens
│   └── core-testing/        → Helpers e fakes para testes
└── feature/
    ├── feature-today/       → Tela principal (hábitos do dia)
    ├── feature-detail/      → Detalhes + histórico de um hábito
    ├── feature-manage/      → Criar / editar hábitos
    └── feature-stats/       → Estatísticas e visão geral
```

### Fluxo de dependências

```
feature-* → core-domain ← core-database
feature-* → core-ui
app → feature-* + core-*
```

`core-domain` é um módulo Kotlin puro (sem dependências Android), garantindo que a lógica de negócio seja testável em isolamento.

## 🛠️ Stack Tecnológica

| Camada | Tecnologia |
|--------|-----------|
| Linguagem | Kotlin 100% |
| UI | Jetpack Compose + Material 3 |
| Arquitetura | Clean Architecture + MVI |
| DI | Hilt |
| Banco de dados | Room + SQLite (offline-first) |
| Navegação | Navigation Compose (type-safe routes) |
| Async | Coroutines + Flow |
| Build | Gradle Kotlin DSL + Version Catalog |
| Testes | JUnit5 + MockK + Turbine |

## 🚀 Como rodar

### Pré-requisitos
- Android Studio Hedgehog (2023.1.1) ou superior
- JDK 17
- Android SDK com API 35

### Build
```bash
./gradlew assembleDebug
```

### Testes
```bash
# Testes unitários do domínio
./gradlew :core:core-domain:test

# Todos os testes
./gradlew test
```

## 📱 Features

### Tela Hoje
- Header com data e barra de progresso geral
- Cards de hábito com barra segmentada animada
- Toque para registrar check, toque longo para detalhes
- FAB para criar novo hábito

### Detalhes
- Contribution grid (estilo GitHub)
- Gráfico de barras dos últimos 14 dias (Canvas)
- Estatísticas: streak atual, melhor streak, taxa de conclusão
- Ações: editar e arquivar

### Criar/Editar
- Seletor de emoji e cor
- Stepper para checks por dia
- Preview em tempo real do card

### Estatísticas
- Taxas de completude: hoje, semana, mês
- Destaques: maior streak ativo, mais consistente
- Detalhamento por hábito

## 🎨 Decisões Técnicas

1. **Offline-first**: Zero dependências de rede. Nenhuma permissão necessária.
2. **Type-safe navigation**: Rotas definidas como `@Serializable` classes.
3. **Repository pattern**: Interfaces no domain, implementações no database module.
4. **Streak calculation**: Calculado na camada de domínio — um dia só conta se `checks >= checksPerDay`.
5. **Dynamic Color**: Suporte a cores dinâmicas no Android 12+ com fallback para tema customizado.

## 📝 Licença

Este projeto é de uso pessoal e educacional.

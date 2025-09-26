# Projman API

Sistema de gerenciamento de projetos em Java - backend REST API.

## Stack

- Java 21
- SQLite (banco de dados)
- HttpServer nativo do Java
- Gson (JSON)
- jBCrypt (hash de senhas)
- Gradle (build)

## Como rodar

```bash
# Rodar em modo desenvolvimento (com auto-restart)
./dev-server

# Ou rodar diretamente com Gradle
./gradlew :app:run
```

A API roda por padrão em `http://localhost:8080`.

### Variáveis de ambiente

- `PROJMAN_PORT`: porta da API (padrão: 8080)
- `PROJMAN_DB`: arquivo do banco SQLite (padrão: projman.db)

## Endpoints

- `GET/POST /users` - listar/criar usuários
- `GET/POST /teams` - listar/criar equipes
- `GET/POST /projects` - listar/criar projetos
- `GET/POST /tasks` - listar/criar tarefas
- `PUT /tasks/{id}/status` - atualizar status de tarefa

## Banco de dados

O SQLite é criado automaticamente na primeira execução. As tabelas são:

- `users` - usuários do sistema
- `teams` - equipes
- `team_members` - membros das equipes
- `projects` - projetos
- `project_teams` - equipes dos projetos
- `tasks` - tarefas dos projetos

## Frontend

Este projeto trabalha junto com o frontend React que está na pasta `projman-web/`. Você precisa rodar os dois juntos:

1. Backend: `./dev-server` (nesta pasta)
2. Frontend: `npm run dev` (na pasta projman-web)

## Testes

Para rodar testes isolados use:

```bash
# Limpa banco de teste e roda API na porta 18080
./dev-test-db
```

Os testes E2E estão no projeto React https://github.com/wmunistd/projman-ui e usam este comando automaticamente.

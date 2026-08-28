# tributos-backend

Backend multi-tenant do Sistema de Gestão Tributária Municipal. Java 21 + Spring Boot 4,
arquitetura hexagonal (ver `PLANEJAMENTO_PROJETO.md` no repositório principal do
planejamento, §7).

## Stack

- Java 21, Spring Boot 4.1.1 (Web, Data JPA, Security, Validation, Flyway, Actuator)
- PostgreSQL 16 — multi-tenancy via coluna `tenant_id` + Row Level Security
- Maven multi-módulo, com Maven Wrapper (`./mvnw`) — não é necessário ter Maven instalado
- Testcontainers para testes de integração (sobe um Postgres real e efêmero)

## Estrutura dos módulos

```
backend/
├── shared-kernel/        Java puro: Value Objects (Money), exceções de domínio,
│                          contrato de tenancy (TenantContext, TenantAware). Sem Spring.
├── platform-identity/     Bounded context de Identity & Access (tenant, usuário, papel,
│                          permissão, auditoria). Arquitetura hexagonal:
│                            domain/            — regras de negócio puras
│                            application/       — casos de uso (orquestram portas)
│                            adapters/in/web/    — controllers REST
│                            adapters/out/persistence/ — JPA (Entity, Repository, Adapter)
│                          Migrations Flyway do módulo em
│                          src/main/resources/db/migration/platform/.
└── app-bootstrap/         Único módulo executável — @SpringBootApplication, application.yml,
                           SecurityConfig, GlobalExceptionHandler. Decide a versão real de
                           Spring Boot/Hibernate; os módulos de domínio acima não decidem.
```

Cada futuro módulo tributário (`module-iss`, `module-iptu`, `module-itbi`, ...) segue o
mesmo padrão de `platform-identity`: depende só de `shared-kernel`, nunca de outro módulo
de domínio diretamente, e traz sua própria pasta de migrations Flyway
(`db/migration/<nome-do-modulo>/`) — o Flyway do `app-bootstrap` varre `classpath:db/migration`
e enxerga as subpastas de todos os módulos presentes no classpath automaticamente.

## Rodando localmente

```bash
# 1. Sobe o Postgres local
docker compose up -d

# 2. Copia variáveis de ambiente (valores já batem com o docker-compose.yml)
cp .env.example .env

# 3. Roda a aplicação (baixa dependências no primeiro uso)
./mvnw -pl app-bootstrap spring-boot:run
```

A aplicação sobe em `http://localhost:8080`. As migrations Flyway rodam automaticamente e
semeiam um tenant `demo` (mesmo slug usado como fallback pelo frontend em desenvolvimento),
com um usuário administrador de exemplo (login `admin`, senha `Demo@123` — **só existe em
dev local**, ver `V4__seed_rbac_demo.sql`). Teste rápido:

```bash
curl http://localhost:8080/api/public/tenants/demo/branding

curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -H "X-Tenant-Slug: demo" \
  -d '{"login":"admin","senha":"Demo@123"}'
```

Usuário da equipe da plataforma (cadastro de novas prefeituras — `POST /api/admin/tenants`):

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -H "X-Tenant-Slug: _plataforma" \
  -d '{"login":"plataforma-admin","senha":"Demo@123"}'
```

## Autenticação

Módulo real (`platform-identity`), substituindo o `permitAll()` do Sprint 0 inicial:

- **Login em duas etapas**: `POST /api/auth/login` (header `X-Tenant-Slug`) retorna os
  tokens diretamente, ou — se o usuário tiver MFA habilitado — um `tokenMfaPendente` de
  curta duração que precisa ser confirmado em `POST /api/auth/mfa/verificar` (código TOTP).
- **JWT auto-assinado (HS256)**: access token de 15 min, claims `sub` (id do usuário),
  `tenant_id` e `roles` (usada pelo RBAC via `@PreAuthorize("hasRole(...)")`).
- **Refresh token opaco** (30 dias, hash SHA-256 no banco, rotacionado a cada uso):
  `POST /api/auth/refresh` e `POST /api/auth/logout`.
- **MFA (TOTP/RFC 6238)**: `POST /api/auth/mfa/habilitar` (usuário autenticado, gera
  segredo + URI de provisionamento para app autenticador) e
  `POST /api/auth/mfa/confirmar` (código TOTP) para efetivar.
- **RBAC**: papéis-base `ADMIN_TENANT`, `FISCAL`, `ATENDENTE` semeados por
  `V4__seed_rbac_demo.sql`; catálogo de permissões granular (`modulo:recurso:acao`) pensado
  para uma evolução futura a ABAC sem mudar a tabela.
- **Multi-tenancy end-to-end**: `TenantContextFilter` lê `tenant_id` do JWT já validado e
  popula `TenantContext`, usado por `TenantAwareDataSource` para `SET LOCAL
  app.current_tenant` em cada conexão (RLS).

Variáveis de ambiente relevantes (ver `application.yml`): `APP_SECURITY_JWT_SECRET`
(obrigatório em produção, >= 32 bytes), `APP_SECURITY_JWT_EMISSOR`,
`APP_SECURITY_MFA_EMISSOR`.

## Observabilidade

- **Correlação de logs**: `RequestIdFilter` gera (ou propaga, se já vier de um proxy/gateway)
  um `X-Request-Id` por requisição, devolvido no header de resposta e disponível no MDC
  (`requestId`) em toda linha de log daquela requisição — inclusive quando ela é rejeitada
  antes de chegar num controller (o filtro roda antes da cadeia do Spring Security).
- **Health-check**: `GET /actuator/health` (com detalhes, incluindo indicador de banco) e os
  grupos padrão Kubernetes `GET /actuator/health/liveness` / `GET /actuator/health/readiness`.
- **Logs estruturados em produção**: defina `LOGGING_STRUCTURED_FORMAT_CONSOLE=ecs` (ou
  `logstash`) para trocar o console para JSON — recurso nativo do Spring Boot (>= 3.4), sem
  dependência extra; o MDC (`requestId`) entra automaticamente como campo. Em dev, o padrão é
  texto legível com `[requestId]` embutido.
- **Documentação da API**: Swagger UI em `http://localhost:8080/swagger-ui.html`
  (`/v3/api-docs` para o JSON puro) — inclui botão "Authorize" para colar o `accessToken`
  obtido no login e testar endpoints protegidos direto pela UI.

## Testes

```bash
./mvnw test
```

O teste de integração de `platform-identity`/`app-bootstrap` (`TenantPublicControllerTest`)
usa Testcontainers e precisa de Docker rodando na máquina.

## Segurança — pendências conhecidas do Sprint 0

- `SecurityConfig` agora é um Resource Server JWT real (`anyRequest().authenticated()`,
  só `/api/public/**` e os endpoints não-autenticados de `/api/auth/**` são públicos) —
  ver seção "Autenticação" acima. MFA por e-mail (`TipoMfa.EMAIL`) ainda não tem adapter,
  só o enum existe; só TOTP está implementado.
- O isolamento por RLS depende de uma role de banco de dados de aplicação **sem** o
  atributo `BYPASSRLS`. Em desenvolvimento local o `docker-compose.yml` usa o usuário
  padrão do Postgres (superusuário, que ignora RLS) só para simplificar o ambiente — não
  reflete a configuração de produção, que precisa de uma role dedicada.
- Chave JWT é HMAC simétrica (HS256) auto-assinada — suficiente para o Sprint 0 (sem
  Authorization Server externo), mas a migração para chave assimétrica/Keycloak, se algum
  dia necessária, muda só `JwtGeradorToken` e `SecurityConfig.jwtDecoder`.

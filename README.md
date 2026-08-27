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
semeiam um tenant `demo` (mesmo slug usado como fallback pelo frontend em desenvolvimento).
Teste rápido:

```bash
curl http://localhost:8080/api/public/tenants/demo/branding
```

## Testes

```bash
./mvnw test
```

O teste de integração de `platform-identity`/`app-bootstrap` (`TenantPublicControllerTest`)
usa Testcontainers e precisa de Docker rodando na máquina.

## Segurança — pendências conhecidas do Sprint 0

- `SecurityConfig` libera todas as rotas (`anyRequest().permitAll()`) até o módulo de
  autenticação (JWT + RBAC/ABAC) ser implementado — ver roadmap, Sprint 0.
- O isolamento por RLS depende de uma role de banco de dados de aplicação **sem** o
  atributo `BYPASSRLS`. Em desenvolvimento local o `docker-compose.yml` usa o usuário
  padrão do Postgres (superusuário, que ignora RLS) só para simplificar o ambiente — não
  reflete a configuração de produção, que precisa de uma role dedicada.

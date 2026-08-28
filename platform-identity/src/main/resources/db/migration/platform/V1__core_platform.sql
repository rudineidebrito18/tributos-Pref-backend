-- ============================================================================
-- SPRINT 0 — Fundação da plataforma: tenant, identidade, RBAC/ABAC, auditoria.
-- Ver PLANEJAMENTO_PROJETO.md §5.1 (modelo de dados) e §6 (multi-tenancy via RLS).
-- ============================================================================

CREATE EXTENSION IF NOT EXISTS pgcrypto; -- gen_random_uuid() em versões de Postgres < 13

-- ----------------------------------------------------------------------------
-- Registro de tenants — NÃO tem tenant_id/RLS: é a própria tabela que define os
-- tenants. Consultada sem qualquer isolamento pelo endpoint público de branding
-- (GET /api/public/tenants/{slug}/branding).
-- ----------------------------------------------------------------------------
CREATE TABLE tenant (
    id                    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    slug                  VARCHAR(100) NOT NULL UNIQUE,
    nome                  VARCHAR(200) NOT NULL,
    uf                    VARCHAR(2) NOT NULL,
    tipo_entidade         VARCHAR(20) NOT NULL CHECK (tipo_entidade IN ('PREFEITURA', 'CAMARA')),
    logo_url              VARCHAR(500),
    cor_accent            VARCHAR(9),
    cor_accent_dark       VARCHAR(9),
    cor_accent_secondary  VARCHAR(9),
    cor_accent_tertiary   VARCHAR(9),
    ativo                 BOOLEAN NOT NULL DEFAULT true,
    criado_em             TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Módulos tributários habilitados por tenant (equivalente multi-tenant do `modulos.ts`
-- estático do portal de transparência — aqui é dado, não registro versionado no código,
-- porque um mesmo deploy atende tenants com módulos contratados diferentes).
CREATE TABLE tenant_modulo_ativo (
    tenant_id  UUID NOT NULL REFERENCES tenant(id) ON DELETE CASCADE,
    modulo_id  VARCHAR(50) NOT NULL,
    PRIMARY KEY (tenant_id, modulo_id)
);

-- ----------------------------------------------------------------------------
-- Helper de RLS — evita repetir as três instruções (ENABLE/FORCE/CREATE POLICY) por
-- tabela multi-tenant. Assume o padrão: coluna `tenant_id UUID NOT NULL`, e que a sessão
-- da aplicação executa `SET LOCAL app.current_tenant = '<uuid>'` no início de cada
-- transação (ver TenantContext no shared-kernel + interceptor JDBC, a implementar em
-- app-bootstrap quando o primeiro módulo de negócio autenticado existir).
--
-- `FORCE ROW LEVEL SECURITY`: vale até para o owner da tabela. Não protege, porém, contra
-- um papel de banco com atributo `BYPASSRLS` (ex.: superusuário) — a role usada pela
-- aplicação em produção precisa ser criada SEM esse atributo (pendência de infraestrutura,
-- fora do escopo desta migration; em dev local a conexão via docker-compose usa o usuário
-- `postgres`, que é superusuário e portanto ilustra a estrutura mas não a proteção real).
-- ----------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION aplicar_isolamento_tenant(nome_tabela text) RETURNS void AS $$
BEGIN
    EXECUTE format('ALTER TABLE %I ENABLE ROW LEVEL SECURITY', nome_tabela);
    EXECUTE format('ALTER TABLE %I FORCE ROW LEVEL SECURITY', nome_tabela);
    EXECUTE format(
        'CREATE POLICY tenant_isolation_%1$s ON %1$s '
        || 'USING (tenant_id = current_setting(''app.current_tenant'', true)::uuid) '
        || 'WITH CHECK (tenant_id = current_setting(''app.current_tenant'', true)::uuid)',
        nome_tabela
    );
END;
$$ LANGUAGE plpgsql;

-- ----------------------------------------------------------------------------
-- Identity & Access
-- ----------------------------------------------------------------------------
CREATE TABLE usuario (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL REFERENCES tenant(id),
    login           VARCHAR(150) NOT NULL,
    email           VARCHAR(255) NOT NULL,
    senha_hash      VARCHAR(255) NOT NULL,
    mfa_habilitado  BOOLEAN NOT NULL DEFAULT false,
    mfa_tipo        VARCHAR(20),
    mfa_secret      VARCHAR(64),
    ativo           BOOLEAN NOT NULL DEFAULT true,
    criado_em       TIMESTAMPTZ NOT NULL DEFAULT now(),
    atualizado_em   TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, login),
    UNIQUE (tenant_id, email)
);
SELECT aplicar_isolamento_tenant('usuario');

-- `tenant_id` nulo = papel global (ex.: SUPORTE, usado pela equipe da plataforma, não de
-- uma prefeitura específica). A policy é escrita à mão (não via helper) por isso: papel
-- global deve ser visível a qualquer tenant autenticado, além dos papéis do próprio tenant.
CREATE TABLE papel (
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id  UUID REFERENCES tenant(id),
    nome       VARCHAR(100) NOT NULL,
    descricao  VARCHAR(255)
);
ALTER TABLE papel ENABLE ROW LEVEL SECURITY;
ALTER TABLE papel FORCE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation_papel ON papel
    USING (tenant_id IS NULL OR tenant_id = current_setting('app.current_tenant', true)::uuid)
    WITH CHECK (tenant_id IS NULL OR tenant_id = current_setting('app.current_tenant', true)::uuid);

CREATE TABLE permissao (
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    codigo     VARCHAR(100) NOT NULL UNIQUE,
    descricao  VARCHAR(255),
    modulo     VARCHAR(50)
);
-- Sem tenant_id: catálogo global de permissões (o mesmo conjunto de códigos existe para
-- todos os tenants; o que varia por tenant é qual papel tem qual permissão).

-- Join tables sem coluna tenant_id própria — a proteção vem de papel/usuario já terem RLS.
-- Ficam de fora do isolamento direto nesta migration; se algum caso de uso passar a
-- consultá-las isoladamente (sem join), revisitar e adicionar tenant_id + policy própria.
CREATE TABLE papel_permissao (
    papel_id       UUID NOT NULL REFERENCES papel(id) ON DELETE CASCADE,
    permissao_id   UUID NOT NULL REFERENCES permissao(id) ON DELETE CASCADE,
    condicao_abac  JSONB,
    PRIMARY KEY (papel_id, permissao_id)
);

CREATE TABLE usuario_papel (
    usuario_id  UUID NOT NULL REFERENCES usuario(id) ON DELETE CASCADE,
    papel_id    UUID NOT NULL REFERENCES papel(id) ON DELETE CASCADE,
    PRIMARY KEY (usuario_id, papel_id)
);

CREATE TABLE log_auditoria (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id     UUID NOT NULL REFERENCES tenant(id),
    usuario_id    UUID REFERENCES usuario(id),
    entidade      VARCHAR(100) NOT NULL,
    entidade_id   VARCHAR(100),
    acao          VARCHAR(50) NOT NULL,
    dados_antes   JSONB,
    dados_depois  JSONB,
    criado_em     TIMESTAMPTZ NOT NULL DEFAULT now()
);
SELECT aplicar_isolamento_tenant('log_auditoria');

-- ============================================================================
-- SPRINT 1 — Cadastro Único: território, pessoas, endereços e documentos.
-- Ver PLANEJAMENTO_PROJETO.md §5.2.
-- ============================================================================

-- Referência nacional — sem tenant_id/RLS (compartilhada entre todos os tenants).
CREATE TABLE estado (
    id    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    sigla VARCHAR(2) NOT NULL UNIQUE,
    nome  VARCHAR(100) NOT NULL
);

CREATE TABLE cidade (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    estado_id    UUID NOT NULL REFERENCES estado(id),
    nome         VARCHAR(200) NOT NULL,
    codigo_ibge  VARCHAR(7),
    UNIQUE (estado_id, nome)
);

CREATE INDEX idx_cidade_estado ON cidade(estado_id);

CREATE TABLE bairro (
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id  UUID NOT NULL REFERENCES tenant(id),
    cidade_id  UUID NOT NULL REFERENCES cidade(id),
    nome       VARCHAR(200) NOT NULL,
    criado_em  TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, cidade_id, nome)
);
SELECT aplicar_isolamento_tenant('bairro');

CREATE TABLE logradouro (
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id  UUID NOT NULL REFERENCES tenant(id),
    cidade_id  UUID NOT NULL REFERENCES cidade(id),
    bairro_id  UUID REFERENCES bairro(id),
    tipo       VARCHAR(50),
    nome       VARCHAR(200) NOT NULL,
    cep        VARCHAR(8),
    criado_em  TIMESTAMPTZ NOT NULL DEFAULT now()
);
SELECT aplicar_isolamento_tenant('logradouro');

CREATE TABLE pessoa (
    id                       UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id                UUID NOT NULL REFERENCES tenant(id),
    tipo_pessoa              VARCHAR(2) NOT NULL CHECK (tipo_pessoa IN ('PF', 'PJ')),
    cpf_cnpj                 VARCHAR(14) NOT NULL,
    nome                     VARCHAR(200) NOT NULL,
    nome_fantasia            VARCHAR(200),
    razao_social             VARCHAR(200),
    data_nascimento_fundacao DATE,
    email                    VARCHAR(255),
    telefone1                VARCHAR(20),
    telefone2                VARCHAR(20),
    ativo                    BOOLEAN NOT NULL DEFAULT true,
    criado_em                TIMESTAMPTZ NOT NULL DEFAULT now(),
    atualizado_em            TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, cpf_cnpj)
);
SELECT aplicar_isolamento_tenant('pessoa');

CREATE INDEX idx_pessoa_nome ON pessoa(tenant_id, nome);

CREATE TABLE endereco (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id        UUID NOT NULL REFERENCES tenant(id),
    pessoa_id        UUID NOT NULL REFERENCES pessoa(id) ON DELETE CASCADE,
    logradouro_id    UUID REFERENCES logradouro(id),
    logradouro_texto VARCHAR(300),
    numero           VARCHAR(20),
    complemento      VARCHAR(100),
    bairro_id        UUID REFERENCES bairro(id),
    bairro_texto     VARCHAR(200),
    cidade_id        UUID REFERENCES cidade(id),
    cep              VARCHAR(8),
    principal        BOOLEAN NOT NULL DEFAULT false,
    criado_em        TIMESTAMPTZ NOT NULL DEFAULT now()
);
SELECT aplicar_isolamento_tenant('endereco');

CREATE TABLE documento (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id     UUID NOT NULL REFERENCES tenant(id),
    pessoa_id     UUID NOT NULL REFERENCES pessoa(id) ON DELETE CASCADE,
    tipo          VARCHAR(100) NOT NULL,
    nome_arquivo  VARCHAR(300) NOT NULL,
    conteudo_tipo VARCHAR(100) NOT NULL,
    tamanho_bytes BIGINT NOT NULL,
    storage_chave VARCHAR(500) NOT NULL,
    compartilhado BOOLEAN NOT NULL DEFAULT false,
    criado_em     TIMESTAMPTZ NOT NULL DEFAULT now()
);
SELECT aplicar_isolamento_tenant('documento');

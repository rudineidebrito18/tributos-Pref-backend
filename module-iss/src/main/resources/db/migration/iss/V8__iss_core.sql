-- ============================================================================
-- SPRINT 2 — ISSQN: contribuintes, credenciamento e catálogos auxiliares.
-- Ver PLANEJAMENTO_PROJETO.md §5.3 e ROADMAP Sprint 2.
-- ============================================================================

CREATE TABLE iss_tipo_contribuinte (
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id  UUID NOT NULL REFERENCES tenant(id),
    nome       VARCHAR(200) NOT NULL,
    ativo      BOOLEAN NOT NULL DEFAULT true,
    criado_em  TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, nome)
);
SELECT aplicar_isolamento_tenant('iss_tipo_contribuinte');

CREATE TABLE iss_situacao_cadastral (
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id  UUID NOT NULL REFERENCES tenant(id),
    nome       VARCHAR(100) NOT NULL,
    ativo      BOOLEAN NOT NULL DEFAULT true,
    criado_em  TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, nome)
);
SELECT aplicar_isolamento_tenant('iss_situacao_cadastral');

CREATE TABLE iss_status_credenciamento (
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id  UUID NOT NULL REFERENCES tenant(id),
    nome       VARCHAR(100) NOT NULL,
    ativo      BOOLEAN NOT NULL DEFAULT true,
    criado_em  TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, nome)
);
SELECT aplicar_isolamento_tenant('iss_status_credenciamento');

CREATE TABLE iss_regime_tributario (
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id  UUID NOT NULL REFERENCES tenant(id),
    nome       VARCHAR(100) NOT NULL,
    ativo      BOOLEAN NOT NULL DEFAULT true,
    criado_em  TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, nome)
);
SELECT aplicar_isolamento_tenant('iss_regime_tributario');

CREATE TABLE iss_contribuinte (
    id                        UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id                 UUID NOT NULL REFERENCES tenant(id),
    pessoa_id                 UUID NOT NULL REFERENCES pessoa(id),
    inscricao_municipal       VARCHAR(20) NOT NULL,
    tipo_contribuinte_id      UUID NOT NULL REFERENCES iss_tipo_contribuinte(id),
    situacao_cadastral_id     UUID NOT NULL REFERENCES iss_situacao_cadastral(id),
    status_credenciamento_id  UUID NOT NULL REFERENCES iss_status_credenciamento(id),
    regime_tributario_id      UUID NOT NULL REFERENCES iss_regime_tributario(id),
    nome_contador             VARCHAR(200),
    email_contador            VARCHAR(255),
    criado_em                 TIMESTAMPTZ NOT NULL DEFAULT now(),
    atualizado_em             TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, inscricao_municipal),
    UNIQUE (tenant_id, pessoa_id)
);
SELECT aplicar_isolamento_tenant('iss_contribuinte');

CREATE INDEX idx_iss_contribuinte_pessoa ON iss_contribuinte(tenant_id, pessoa_id);

CREATE TABLE iss_solicitacao_credenciamento (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id        UUID NOT NULL REFERENCES tenant(id),
    contribuinte_id  UUID NOT NULL REFERENCES iss_contribuinte(id),
    status_id        UUID NOT NULL REFERENCES iss_status_credenciamento(id),
    observacao       TEXT,
    analisado_por    UUID REFERENCES usuario(id),
    analisado_em     TIMESTAMPTZ,
    criado_em        TIMESTAMPTZ NOT NULL DEFAULT now()
);
SELECT aplicar_isolamento_tenant('iss_solicitacao_credenciamento');

CREATE INDEX idx_iss_solicitacao_contribuinte ON iss_solicitacao_credenciamento(tenant_id, contribuinte_id);

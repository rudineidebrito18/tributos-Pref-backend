-- ============================================================================
-- SPRINT 5 — ISSQN: tipos de alvará, alvarás e certidões.
-- Ver PLANEJAMENTO_PROJETO.md §5.3 e ROADMAP Sprint 5.
-- ============================================================================

CREATE TABLE iss_tipo_alvara (
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id      UUID NOT NULL REFERENCES tenant(id),
    nome           VARCHAR(150) NOT NULL,
    valor_base     NUMERIC(14, 2) NOT NULL DEFAULT 0,
    dias_validade  INTEGER NOT NULL DEFAULT 365 CHECK (dias_validade > 0),
    ativo          BOOLEAN NOT NULL DEFAULT true,
    UNIQUE (tenant_id, nome)
);
SELECT aplicar_isolamento_tenant('iss_tipo_alvara');

CREATE TABLE iss_alvara (
    id                   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id            UUID NOT NULL REFERENCES tenant(id),
    numero               BIGINT NOT NULL,
    tipo_alvara_id       UUID NOT NULL REFERENCES iss_tipo_alvara(id),
    contribuinte_id      UUID NOT NULL REFERENCES iss_contribuinte(id),
    data_expedicao       DATE NOT NULL,
    validade             DATE NOT NULL,
    situacao_fiscal      VARCHAR(20) NOT NULL CHECK (situacao_fiscal IN ('REGULAR', 'IRREGULAR')),
    valor                NUMERIC(14, 2) NOT NULL,
    codigo_verificacao   VARCHAR(32) NOT NULL UNIQUE,
    data_emissao         TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, numero),
    CHECK (validade >= data_expedicao)
);
SELECT aplicar_isolamento_tenant('iss_alvara');

CREATE INDEX idx_iss_alvara_contribuinte ON iss_alvara(tenant_id, contribuinte_id);
CREATE INDEX idx_iss_alvara_codigo ON iss_alvara(codigo_verificacao);

CREATE TABLE iss_certidao (
    id                   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id            UUID NOT NULL REFERENCES tenant(id),
    tipo                 VARCHAR(20) NOT NULL CHECK (tipo IN ('NADA_CONSTA', 'SITUACAO_CND')),
    contribuinte_id      UUID NOT NULL REFERENCES iss_contribuinte(id),
    numero               BIGINT NOT NULL,
    codigo_verificacao   VARCHAR(32) NOT NULL UNIQUE,
    data_emissao         TIMESTAMPTZ NOT NULL DEFAULT now(),
    validade             DATE NOT NULL,
    UNIQUE (tenant_id, numero)
);
SELECT aplicar_isolamento_tenant('iss_certidao');

CREATE INDEX idx_iss_certidao_contribuinte ON iss_certidao(tenant_id, contribuinte_id);
CREATE INDEX idx_iss_certidao_codigo ON iss_certidao(codigo_verificacao);

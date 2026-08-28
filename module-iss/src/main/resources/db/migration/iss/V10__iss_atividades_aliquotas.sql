-- ============================================================================
-- SPRINT 3 — ISSQN: atividades, serviços (LC 116), vínculos e alíquotas por regime.
-- Ver PLANEJAMENTO_PROJETO.md §5.3 e ROADMAP Sprint 3.
-- ============================================================================

CREATE TABLE iss_atividade (
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id  UUID NOT NULL REFERENCES tenant(id),
    codigo     VARCHAR(20) NOT NULL,
    descricao  VARCHAR(500) NOT NULL,
    ativo      BOOLEAN NOT NULL DEFAULT true,
    criado_em  TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, codigo)
);
SELECT aplicar_isolamento_tenant('iss_atividade');

CREATE TABLE iss_servico (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL REFERENCES tenant(id),
    codigo_lc116    VARCHAR(10) NOT NULL,
    descricao       VARCHAR(500) NOT NULL,
    aliquota_minima NUMERIC(8, 4),
    aliquota_maxima NUMERIC(8, 4),
    ativo           BOOLEAN NOT NULL DEFAULT true,
    criado_em       TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, codigo_lc116)
);
SELECT aplicar_isolamento_tenant('iss_servico');

CREATE TABLE iss_contribuinte_atividade_servico (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id        UUID NOT NULL REFERENCES tenant(id),
    contribuinte_id  UUID NOT NULL REFERENCES iss_contribuinte(id) ON DELETE CASCADE,
    atividade_id     UUID NOT NULL REFERENCES iss_atividade(id),
    servico_id       UUID NOT NULL REFERENCES iss_servico(id),
    tributavel       BOOLEAN NOT NULL DEFAULT true,
    criado_em        TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, contribuinte_id, atividade_id, servico_id)
);
SELECT aplicar_isolamento_tenant('iss_contribuinte_atividade_servico');

CREATE TABLE iss_aliquota_regime (
    id                   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id            UUID NOT NULL REFERENCES tenant(id),
    regime_id            UUID NOT NULL REFERENCES iss_regime_tributario(id),
    faixa_receita_min    NUMERIC(14, 2) NOT NULL,
    faixa_receita_max    NUMERIC(14, 2),
    aliquota_nominal     NUMERIC(8, 4) NOT NULL,
    parcela_deduzir      NUMERIC(14, 2) NOT NULL DEFAULT 0,
    percentual_iss       NUMERIC(8, 4) NOT NULL DEFAULT 33.5,
    competencia_vigencia DATE NOT NULL,
    anexo_simples        VARCHAR(10),
    criado_em            TIMESTAMPTZ NOT NULL DEFAULT now()
);
SELECT aplicar_isolamento_tenant('iss_aliquota_regime');

CREATE INDEX idx_iss_aliquota_regime_regime ON iss_aliquota_regime(tenant_id, regime_id, competencia_vigencia);

-- ============================================================================
-- SPRINT 4 — ISSQN: tomadores e notas fiscais de serviço (NFS-e).
-- Ver PLANEJAMENTO_PROJETO.md §5.3 e ROADMAP Sprint 4.
-- ============================================================================

CREATE TABLE iss_tomador (
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id  UUID NOT NULL REFERENCES tenant(id),
    pessoa_id  UUID NOT NULL REFERENCES pessoa(id),
    criado_em  TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, pessoa_id)
);
SELECT aplicar_isolamento_tenant('iss_tomador');

CREATE INDEX idx_iss_tomador_pessoa ON iss_tomador(tenant_id, pessoa_id);

CREATE TABLE iss_nota_fiscal (
    id                   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id            UUID NOT NULL REFERENCES tenant(id),
    numero               BIGINT NOT NULL,
    serie                VARCHAR(10) NOT NULL DEFAULT '1',
    contribuinte_id      UUID NOT NULL REFERENCES iss_contribuinte(id),
    tomador_id           UUID NOT NULL REFERENCES iss_tomador(id),
    servico_id           UUID NOT NULL REFERENCES iss_servico(id),
    competencia          DATE NOT NULL,
    valor_servico        NUMERIC(14, 2) NOT NULL,
    valor_deducoes       NUMERIC(14, 2) NOT NULL DEFAULT 0,
    base_calculo         NUMERIC(14, 2) NOT NULL,
    aliquota_aplicada    NUMERIC(10, 6) NOT NULL,
    valor_iss            NUMERIC(14, 2) NOT NULL,
    status               VARCHAR(20) NOT NULL CHECK (status IN ('EMITIDA', 'CANCELADA', 'SUBSTITUIDA')),
    nota_substituta_id   UUID REFERENCES iss_nota_fiscal(id),
    motivo_cancelamento  TEXT,
    data_emissao         TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, numero)
);
SELECT aplicar_isolamento_tenant('iss_nota_fiscal');

CREATE INDEX idx_iss_nota_fiscal_contribuinte ON iss_nota_fiscal(tenant_id, contribuinte_id, competencia);
CREATE INDEX idx_iss_nota_fiscal_tomador ON iss_nota_fiscal(tenant_id, tomador_id);
CREATE INDEX idx_iss_nota_fiscal_status ON iss_nota_fiscal(tenant_id, status);

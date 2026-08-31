CREATE TABLE iss_atividade_servico (
    id                    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id             UUID NOT NULL REFERENCES tenant(id),
    atividade_id          UUID NOT NULL REFERENCES iss_atividade(id),
    servico_id            UUID REFERENCES iss_servico(id),
    local_incidencia_id   UUID NOT NULL REFERENCES iss_local_incidencia(id),
    aliquota              NUMERIC(10,6) NOT NULL CHECK (aliquota >= 0 AND aliquota <= 100),
    tributavel            BOOLEAN NOT NULL DEFAULT true,
    imune                 BOOLEAN NOT NULL DEFAULT false,
    deducao               BOOLEAN NOT NULL DEFAULT false,
    substituto_tributario BOOLEAN NOT NULL DEFAULT false,
    retencao_fonte        BOOLEAN NOT NULL DEFAULT false,
    regime_especial       VARCHAR(200),
    observacao            TEXT,
    criado_em             TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, atividade_id, servico_id)
);
SELECT aplicar_isolamento_tenant('iss_atividade_servico');
CREATE INDEX idx_iss_ativserv_atividade ON iss_atividade_servico(tenant_id, atividade_id);

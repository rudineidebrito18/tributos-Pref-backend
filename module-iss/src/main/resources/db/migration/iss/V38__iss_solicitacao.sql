CREATE TABLE iss_solicitacao (
    id                     UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id              UUID NOT NULL REFERENCES tenant(id),
    usuario_id             UUID NOT NULL REFERENCES usuario(id),
    tipo_solicitacao_id    UUID NOT NULL REFERENCES iss_tipo_solicitacao(id),
    status_solicitacao_id  UUID NOT NULL REFERENCES iss_status_solicitacao(id),
    descricao              TEXT NOT NULL,
    data_hora              TIMESTAMPTZ NOT NULL,
    criado_em              TIMESTAMPTZ NOT NULL DEFAULT now()
);
SELECT aplicar_isolamento_tenant('iss_solicitacao');
CREATE INDEX idx_iss_solicitacao_status ON iss_solicitacao(tenant_id, status_solicitacao_id);

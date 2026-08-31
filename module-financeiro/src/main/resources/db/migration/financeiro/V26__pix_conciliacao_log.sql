CREATE TABLE pix_conciliacao_log (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL REFERENCES tenant(id),
    guia_id         UUID REFERENCES guia_arrecadacao(id),
    txid            VARCHAR(100),
    end_to_end_id   VARCHAR(100),
    status_anterior VARCHAR(40),
    status_novo     VARCHAR(40),
    origem          VARCHAR(20) NOT NULL CHECK (origem IN ('WEBHOOK', 'CONSULTA', 'MANUAL')),
    payload_bruto   JSONB,
    criado_em       TIMESTAMPTZ NOT NULL DEFAULT now()
);

SELECT aplicar_isolamento_tenant('pix_conciliacao_log');

CREATE INDEX idx_pix_concil_txid ON pix_conciliacao_log(tenant_id, txid);
CREATE INDEX idx_pix_concil_end_to_end ON pix_conciliacao_log(tenant_id, end_to_end_id);

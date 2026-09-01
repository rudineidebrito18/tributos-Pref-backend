CREATE TABLE itbi_guia_parte (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL REFERENCES tenant(id),
    guia_id         UUID NOT NULL REFERENCES itbi_guia(id) ON DELETE CASCADE,
    contribuinte_id UUID NOT NULL REFERENCES iss_contribuinte(id),
    papel           VARCHAR(15) NOT NULL CHECK (papel IN ('TRANSMITENTE','ADQUIRENTE')),
    porcentagem     NUMERIC(6,2) NOT NULL CHECK (porcentagem > 0 AND porcentagem <= 100),
    principal       BOOLEAN NOT NULL DEFAULT false,
    UNIQUE (guia_id, contribuinte_id, papel)
);
SELECT aplicar_isolamento_tenant('itbi_guia_parte');
CREATE INDEX idx_itbi_parte_guia ON itbi_guia_parte(tenant_id, guia_id, papel);

ALTER TABLE itbi_guia ADD COLUMN data_transacao DATE;
ALTER TABLE itbi_guia ADD COLUMN percentual_transmitido NUMERIC(6,2) NOT NULL DEFAULT 100;
ALTER TABLE itbi_guia ADD COLUMN valor_nao_financiado NUMERIC(14,2) NOT NULL DEFAULT 0;
ALTER TABLE itbi_guia ADD COLUMN valor_financiado NUMERIC(14,2) NOT NULL DEFAULT 0;
ALTER TABLE itbi_guia ADD COLUMN desconto NUMERIC(14,2) NOT NULL DEFAULT 0;
ALTER TABLE itbi_guia ADD COLUMN tipo_tributacao VARCHAR(20)
    CHECK (tipo_tributacao IN ('TRIBUTAVEL','ISENTO','IMUNE'));
ALTER TABLE itbi_guia ADD COLUMN observacao TEXT;
ALTER TABLE itbi_guia ADD COLUMN motivo_cancelamento TEXT;
ALTER TABLE itbi_guia ADD COLUMN codigo_verificacao VARCHAR(32);
ALTER TABLE itbi_guia ALTER COLUMN adquirente_id DROP NOT NULL;

INSERT INTO itbi_guia_parte (tenant_id, guia_id, contribuinte_id, papel, porcentagem, principal)
SELECT g.tenant_id, g.id, c.id, 'ADQUIRENTE', 100, true
FROM itbi_guia g
JOIN iss_contribuinte c ON c.pessoa_id = g.adquirente_id AND c.tenant_id = g.tenant_id
WHERE g.adquirente_id IS NOT NULL;

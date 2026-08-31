-- E4.9 — Certidão negativa: situação CND, observação, avulsa e tributos selecionados.

ALTER TABLE iss_certidao ADD COLUMN situacao_cnd_id UUID REFERENCES iss_situacao_cnd(id);
ALTER TABLE iss_certidao ADD COLUMN observacao TEXT;
ALTER TABLE iss_certidao ADD COLUMN avulsa BOOLEAN NOT NULL DEFAULT false;

CREATE TABLE iss_certidao_tributo (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id    UUID NOT NULL REFERENCES tenant(id),
    certidao_id  UUID NOT NULL REFERENCES iss_certidao(id) ON DELETE CASCADE,
    tributo      VARCHAR(20) NOT NULL
                 CHECK (tributo IN ('ALVARA','HABITE_SE','IPTU','IRPF','IRPJ','ISS','ITBI')),
    UNIQUE (certidao_id, tributo)
);
SELECT aplicar_isolamento_tenant('iss_certidao_tributo');

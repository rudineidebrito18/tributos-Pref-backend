CREATE TABLE itbi_tipo_guia (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id   UUID NOT NULL REFERENCES tenant(id),
    nome        VARCHAR(100) NOT NULL,
    aliquota    NUMERIC(10, 6) NOT NULL DEFAULT 0.02,
    ativo       BOOLEAN NOT NULL DEFAULT TRUE,
    UNIQUE (tenant_id, nome)
);

CREATE TABLE itbi_natureza_transmissao (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id   UUID NOT NULL REFERENCES tenant(id),
    nome        VARCHAR(100) NOT NULL,
    ativo       BOOLEAN NOT NULL DEFAULT TRUE,
    UNIQUE (tenant_id, nome)
);

CREATE TABLE itbi_guia (
    id                                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id                           UUID NOT NULL REFERENCES tenant(id),
    numero                              BIGINT NOT NULL,
    imovel_id                           UUID NOT NULL REFERENCES imovel(id),
    adquirente_id                       UUID NOT NULL,
    tipo_guia_id                        UUID NOT NULL REFERENCES itbi_tipo_guia(id),
    natureza_transmissao_id             UUID NOT NULL REFERENCES itbi_natureza_transmissao(id),
    data_solicitacao                    TIMESTAMPTZ NOT NULL,
    valor_transacao                     NUMERIC(14, 2) NOT NULL,
    valor_venal_referencia              NUMERIC(14, 2) NOT NULL,
    base_calculo                        NUMERIC(14, 2) NOT NULL,
    aliquota                            NUMERIC(10, 6) NOT NULL,
    valor_itbi                          NUMERIC(14, 2) NOT NULL,
    situacao                            VARCHAR(30) NOT NULL,
    transferencia_titularidade_realizada BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT uq_itbi_guia_tenant_numero UNIQUE (tenant_id, numero)
);

CREATE INDEX idx_itbi_guia_imovel ON itbi_guia (tenant_id, imovel_id);
CREATE INDEX idx_itbi_guia_adquirente ON itbi_guia (tenant_id, adquirente_id);

SELECT aplicar_isolamento_tenant('itbi_tipo_guia');
SELECT aplicar_isolamento_tenant('itbi_natureza_transmissao');
SELECT aplicar_isolamento_tenant('itbi_guia');

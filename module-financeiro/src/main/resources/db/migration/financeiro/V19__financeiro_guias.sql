CREATE TABLE forma_pagamento (
    id      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    codigo  VARCHAR(32) NOT NULL UNIQUE,
    nome    VARCHAR(100) NOT NULL
);

CREATE TABLE guia_arrecadacao (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id           UUID NOT NULL REFERENCES tenant(id),
    numero              BIGINT NOT NULL,
    tipo_tributo        VARCHAR(20) NOT NULL,
    origem_tipo         VARCHAR(40),
    origem_id           UUID,
    contribuinte_id     UUID NOT NULL,
    competencia_mes     SMALLINT,
    competencia_ano     SMALLINT,
    data_emissao        TIMESTAMPTZ NOT NULL,
    data_vencimento     DATE NOT NULL,
    valor               NUMERIC(14, 2) NOT NULL,
    situacao            VARCHAR(20) NOT NULL,
    forma_pagamento_id  UUID REFERENCES forma_pagamento(id),
    data_efetivacao     TIMESTAMPTZ,
    valor_pago          NUMERIC(14, 2),
    codigo_barras       VARCHAR(100),
    pix_txid            VARCHAR(100),
    descricao_avulsa    TEXT,
    CONSTRAINT uq_guia_tenant_numero UNIQUE (tenant_id, numero),
    CONSTRAINT uq_guia_tenant_origem UNIQUE (tenant_id, origem_tipo, origem_id)
);

CREATE INDEX idx_guia_arrecadacao_contribuinte ON guia_arrecadacao (tenant_id, contribuinte_id);
CREATE INDEX idx_guia_arrecadacao_situacao ON guia_arrecadacao (tenant_id, situacao);
CREATE INDEX idx_guia_arrecadacao_emissao ON guia_arrecadacao (tenant_id, data_emissao);

SELECT aplicar_isolamento_tenant('guia_arrecadacao');

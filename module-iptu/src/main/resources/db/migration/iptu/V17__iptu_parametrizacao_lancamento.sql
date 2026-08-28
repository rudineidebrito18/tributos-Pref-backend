-- ============================================================================
-- SPRINT 7 — IPTU: parametrização e lançamento anual.
-- ============================================================================

CREATE TABLE iptu_zona_fiscal (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id           UUID NOT NULL REFERENCES tenant(id),
    nome                VARCHAR(150) NOT NULL,
    fator_valorizacao   NUMERIC(10, 4) NOT NULL DEFAULT 1,
    ativo               BOOLEAN NOT NULL DEFAULT true,
    criado_em           TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, nome)
);
SELECT aplicar_isolamento_tenant('iptu_zona_fiscal');

CREATE TABLE iptu_valor_terreno_m2 (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL REFERENCES tenant(id),
    zona_fiscal_id  UUID NOT NULL REFERENCES iptu_zona_fiscal(id),
    exercicio       INT NOT NULL,
    valor_m2        NUMERIC(14, 2) NOT NULL,
    UNIQUE (tenant_id, zona_fiscal_id, exercicio)
);
SELECT aplicar_isolamento_tenant('iptu_valor_terreno_m2');

CREATE INDEX idx_iptu_valor_terreno_exercicio ON iptu_valor_terreno_m2(tenant_id, exercicio);

CREATE TABLE iptu_aliquota (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL REFERENCES tenant(id),
    exercicio       INT NOT NULL,
    destinacao_id   UUID NOT NULL REFERENCES imovel_destinacao(id),
    zona_fiscal_id  UUID NOT NULL REFERENCES iptu_zona_fiscal(id),
    aliquota        NUMERIC(10, 6) NOT NULL,
    UNIQUE (tenant_id, exercicio, destinacao_id, zona_fiscal_id)
);
SELECT aplicar_isolamento_tenant('iptu_aliquota');

CREATE INDEX idx_iptu_aliquota_exercicio ON iptu_aliquota(tenant_id, exercicio);

ALTER TABLE imovel ADD COLUMN zona_fiscal_id UUID REFERENCES iptu_zona_fiscal(id);

CREATE INDEX idx_imovel_zona_fiscal ON imovel(tenant_id, zona_fiscal_id);

CREATE TABLE iptu_lancamento (
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id               UUID NOT NULL REFERENCES tenant(id),
    imovel_id               UUID NOT NULL REFERENCES imovel(id),
    exercicio               INT NOT NULL,
    valor_venal_calculado   NUMERIC(14, 2) NOT NULL,
    aliquota_aplicada       NUMERIC(10, 6) NOT NULL,
    valor_total             NUMERIC(14, 2) NOT NULL,
    numero_parcelas         INT NOT NULL,
    status                  VARCHAR(20) NOT NULL CHECK (status IN ('GERADO', 'CANCELADO')),
    data_geracao            TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, imovel_id, exercicio)
);
SELECT aplicar_isolamento_tenant('iptu_lancamento');

CREATE INDEX idx_iptu_lancamento_exercicio ON iptu_lancamento(tenant_id, exercicio);
CREATE INDEX idx_iptu_lancamento_imovel ON iptu_lancamento(tenant_id, imovel_id);

CREATE TABLE iptu_lancamento_parcela (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL REFERENCES tenant(id),
    lancamento_id   UUID NOT NULL REFERENCES iptu_lancamento(id),
    numero_parcela  INT NOT NULL,
    valor           NUMERIC(14, 2) NOT NULL,
    vencimento      DATE NOT NULL,
    status          VARCHAR(20) NOT NULL CHECK (status IN ('PENDENTE', 'PAGA', 'CANCELADA')),
    UNIQUE (lancamento_id, numero_parcela)
);
SELECT aplicar_isolamento_tenant('iptu_lancamento_parcela');

CREATE INDEX idx_iptu_lancamento_parcela_lancamento ON iptu_lancamento_parcela(lancamento_id);

-- ============================================================================
-- SPRINT 6 — IPTU: cadastro imobiliário, habite-se e certidão negativa.
-- Ver PLANEJAMENTO_PROJETO.md §5.4 e ROADMAP Sprint 6.
-- ============================================================================

CREATE TABLE imovel_tipo (
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id  UUID NOT NULL REFERENCES tenant(id),
    nome       VARCHAR(150) NOT NULL,
    ativo      BOOLEAN NOT NULL DEFAULT true,
    criado_em  TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, nome)
);
SELECT aplicar_isolamento_tenant('imovel_tipo');

CREATE TABLE imovel_tipo_edificacao (
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id  UUID NOT NULL REFERENCES tenant(id),
    nome       VARCHAR(150) NOT NULL,
    ativo      BOOLEAN NOT NULL DEFAULT true,
    criado_em  TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, nome)
);
SELECT aplicar_isolamento_tenant('imovel_tipo_edificacao');

CREATE TABLE imovel_destinacao (
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id  UUID NOT NULL REFERENCES tenant(id),
    nome       VARCHAR(150) NOT NULL,
    ativo      BOOLEAN NOT NULL DEFAULT true,
    criado_em  TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, nome)
);
SELECT aplicar_isolamento_tenant('imovel_destinacao');

CREATE TABLE imovel_tipo_limitacao (
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id  UUID NOT NULL REFERENCES tenant(id),
    nome       VARCHAR(150) NOT NULL,
    ativo      BOOLEAN NOT NULL DEFAULT true,
    criado_em  TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, nome)
);
SELECT aplicar_isolamento_tenant('imovel_tipo_limitacao');

CREATE TABLE imovel_habitese_tipo (
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id  UUID NOT NULL REFERENCES tenant(id),
    nome       VARCHAR(150) NOT NULL,
    ativo      BOOLEAN NOT NULL DEFAULT true,
    criado_em  TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, nome)
);
SELECT aplicar_isolamento_tenant('imovel_habitese_tipo');

CREATE TABLE imovel (
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id               UUID NOT NULL REFERENCES tenant(id),
    numero_cadastro         BIGINT NOT NULL,
    codigo_legado           VARCHAR(50),
    proprietario_id         UUID NOT NULL REFERENCES pessoa(id),
    tipo_id                 UUID NOT NULL REFERENCES imovel_tipo(id),
    endereco_id             UUID REFERENCES endereco(id),
    area_terreno            NUMERIC(14, 4),
    area_construida         NUMERIC(14, 4),
    destinacao_id           UUID REFERENCES imovel_destinacao(id),
    tipo_edificacao_id      UUID REFERENCES imovel_tipo_edificacao(id),
    tipo_limitacao_id       UUID REFERENCES imovel_tipo_limitacao(id),
    valor_venal_terreno     NUMERIC(14, 2) NOT NULL DEFAULT 0,
    valor_venal_construcao  NUMERIC(14, 2) NOT NULL DEFAULT 0,
    situacao                VARCHAR(20) NOT NULL CHECK (situacao IN ('ATIVO', 'INATIVO', 'SUSPENSO')),
    criado_em               TIMESTAMPTZ NOT NULL DEFAULT now(),
    atualizado_em           TIMESTAMPTZ NOT NULL DEFAULT now()
);
SELECT aplicar_isolamento_tenant('imovel');

ALTER TABLE imovel ADD CONSTRAINT uq_imovel_tenant_numero_cadastro UNIQUE (tenant_id, numero_cadastro);

CREATE INDEX idx_imovel_proprietario ON imovel(tenant_id, proprietario_id);
CREATE INDEX idx_imovel_tipo ON imovel(tenant_id, tipo_id);
CREATE INDEX idx_imovel_codigo_legado ON imovel(tenant_id, codigo_legado) WHERE codigo_legado IS NOT NULL;

CREATE TABLE imovel_habitese (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id        UUID NOT NULL REFERENCES tenant(id),
    imovel_id        UUID NOT NULL REFERENCES imovel(id),
    tipo_id          UUID NOT NULL REFERENCES imovel_habitese_tipo(id),
    numero           BIGINT NOT NULL,
    data_emissao     DATE NOT NULL,
    data_emissao_ts  TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, numero)
);
SELECT aplicar_isolamento_tenant('imovel_habitese');

CREATE INDEX idx_imovel_habitese_imovel ON imovel_habitese(tenant_id, imovel_id);

CREATE TABLE imovel_certidao_negativa (
    id                   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id            UUID NOT NULL REFERENCES tenant(id),
    imovel_id            UUID NOT NULL REFERENCES imovel(id),
    numero               BIGINT NOT NULL,
    data_emissao         DATE NOT NULL,
    validade             DATE NOT NULL,
    codigo_verificacao   VARCHAR(32) NOT NULL UNIQUE,
    data_emissao_ts      TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, numero),
    CHECK (validade >= data_emissao)
);
SELECT aplicar_isolamento_tenant('imovel_certidao_negativa');

CREATE INDEX idx_imovel_certidao_imovel ON imovel_certidao_negativa(tenant_id, imovel_id);
CREATE INDEX idx_imovel_certidao_codigo ON imovel_certidao_negativa(codigo_verificacao);

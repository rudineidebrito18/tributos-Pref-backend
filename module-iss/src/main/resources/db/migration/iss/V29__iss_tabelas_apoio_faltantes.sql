-- E3.3 — Tabelas de apoio ISS ausentes (CND, solicitações, local incidência, grupo serviço).

CREATE TABLE iss_situacao_cnd (
    id        UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL REFERENCES tenant(id),
    descricao VARCHAR(200) NOT NULL,
    titulo    VARCHAR(200) NOT NULL,
    ativo     BOOLEAN NOT NULL DEFAULT true,
    criado_em TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, descricao)
);
SELECT aplicar_isolamento_tenant('iss_situacao_cnd');

CREATE TABLE iss_tipo_solicitacao (
    id                    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id             UUID NOT NULL REFERENCES tenant(id),
    descricao             TEXT NOT NULL,
    usuario_notificar_id  UUID REFERENCES usuario(id),
    ativo                 BOOLEAN NOT NULL DEFAULT true,
    criado_em             TIMESTAMPTZ NOT NULL DEFAULT now()
);
SELECT aplicar_isolamento_tenant('iss_tipo_solicitacao');

CREATE TABLE iss_status_solicitacao (
    id        UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL REFERENCES tenant(id),
    descricao TEXT NOT NULL,
    ativo     BOOLEAN NOT NULL DEFAULT true,
    criado_em TIMESTAMPTZ NOT NULL DEFAULT now()
);
SELECT aplicar_isolamento_tenant('iss_status_solicitacao');

CREATE TABLE iss_local_incidencia (
    id        UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL REFERENCES tenant(id),
    descricao VARCHAR(200) NOT NULL,
    ativo     BOOLEAN NOT NULL DEFAULT true,
    criado_em TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, descricao)
);
SELECT aplicar_isolamento_tenant('iss_local_incidencia');

CREATE TABLE iss_grupo_servico (
    id        UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL REFERENCES tenant(id),
    codigo    VARCHAR(10) NOT NULL,
    descricao VARCHAR(300) NOT NULL,
    criado_em TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, codigo)
);
SELECT aplicar_isolamento_tenant('iss_grupo_servico');

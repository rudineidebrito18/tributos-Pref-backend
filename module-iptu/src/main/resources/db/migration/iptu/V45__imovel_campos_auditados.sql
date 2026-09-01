ALTER TABLE imovel ADD COLUMN ano_exercicio SMALLINT;
ALTER TABLE imovel ADD COLUMN data_inclusao DATE;
ALTER TABLE imovel ADD COLUMN area_total NUMERIC(14,4);
ALTER TABLE imovel ADD COLUMN frente NUMERIC(10,2);
ALTER TABLE imovel ADD COLUMN fundos NUMERIC(10,2);
ALTER TABLE imovel ADD COLUMN lado_esquerdo NUMERIC(10,2);
ALTER TABLE imovel ADD COLUMN lado_direito NUMERIC(10,2);
ALTER TABLE imovel ADD COLUMN quadra VARCHAR(20);
ALTER TABLE imovel ADD COLUMN lote VARCHAR(20);
ALTER TABLE imovel ADD COLUMN loteamento VARCHAR(100);
ALTER TABLE imovel ADD COLUMN edificio VARCHAR(100);
ALTER TABLE imovel ADD COLUMN bloco VARCHAR(20);
ALTER TABLE imovel ADD COLUMN sala VARCHAR(20);
ALTER TABLE imovel ADD COLUMN apartamento VARCHAR(20);
ALTER TABLE imovel ADD COLUMN bairro_iptu_id UUID REFERENCES bairro(id);
ALTER TABLE imovel ADD COLUMN logradouro_iptu_id UUID REFERENCES logradouro(id);
ALTER TABLE imovel ADD COLUMN valor_venal_unidade NUMERIC(14,2) NOT NULL DEFAULT 0;
ALTER TABLE imovel ADD COLUMN valor_avaliacao NUMERIC(14,2) NOT NULL DEFAULT 0;
ALTER TABLE imovel ADD COLUMN endereco_correspondencia_id UUID REFERENCES endereco(id);
ALTER TABLE imovel ADD COLUMN observacao TEXT;
ALTER TABLE imovel ALTER COLUMN proprietario_id DROP NOT NULL;

CREATE TABLE imovel_proprietario (
    id                     UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id              UUID NOT NULL REFERENCES tenant(id),
    imovel_id              UUID NOT NULL REFERENCES imovel(id) ON DELETE CASCADE,
    contribuinte_id        UUID NOT NULL REFERENCES iss_contribuinte(id),
    porcentagem            NUMERIC(6,2) NOT NULL CHECK (porcentagem > 0 AND porcentagem <= 100),
    proprietario_principal BOOLEAN NOT NULL DEFAULT false,
    criado_em              TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (imovel_id, contribuinte_id)
);
SELECT aplicar_isolamento_tenant('imovel_proprietario');

CREATE TABLE imovel_titularidade_historico (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL REFERENCES tenant(id),
    imovel_id       UUID NOT NULL REFERENCES imovel(id) ON DELETE CASCADE,
    contribuinte_id UUID NOT NULL REFERENCES iss_contribuinte(id),
    tipo_registro   VARCHAR(20) NOT NULL CHECK (tipo_registro IN ('ENTRADA','SAIDA')),
    porcentagem     NUMERIC(6,2) NOT NULL,
    data_registro   TIMESTAMPTZ NOT NULL DEFAULT now()
);
SELECT aplicar_isolamento_tenant('imovel_titularidade_historico');

CREATE TABLE imovel_observacao (
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id  UUID NOT NULL REFERENCES tenant(id),
    imovel_id  UUID NOT NULL REFERENCES imovel(id) ON DELETE CASCADE,
    usuario_id UUID NOT NULL REFERENCES usuario(id),
    texto      TEXT NOT NULL,
    criado_em  TIMESTAMPTZ NOT NULL DEFAULT now()
);
SELECT aplicar_isolamento_tenant('imovel_observacao');

INSERT INTO imovel_proprietario (tenant_id, imovel_id, contribuinte_id, porcentagem, proprietario_principal)
SELECT i.tenant_id, i.id, c.id, 100, true
FROM imovel i
JOIN iss_contribuinte c ON c.pessoa_id = i.proprietario_id AND c.tenant_id = i.tenant_id
WHERE i.proprietario_id IS NOT NULL;

ALTER TABLE imovel_habitese ADD COLUMN ano SMALLINT;
ALTER TABLE imovel_habitese ADD COLUMN validade DATE;
ALTER TABLE imovel_habitese ADD COLUMN contribuinte_id UUID REFERENCES iss_contribuinte(id);
ALTER TABLE imovel_habitese ADD COLUMN area_imovel NUMERIC(14,4);
ALTER TABLE imovel_habitese ADD COLUMN data_conclusao DATE;
ALTER TABLE imovel_habitese ADD COLUMN numero_alvara VARCHAR(50);
ALTER TABLE imovel_habitese ADD COLUMN data_alvara DATE;
ALTER TABLE imovel_habitese ADD COLUMN validade_alvara DATE;
ALTER TABLE imovel_habitese ADD COLUMN valor_base_calculo NUMERIC(14,2);
ALTER TABLE imovel_habitese ADD COLUMN base_calculo NUMERIC(14,2);
ALTER TABLE imovel_habitese ADD COLUMN desconto NUMERIC(14,2) NOT NULL DEFAULT 0;
ALTER TABLE imovel_habitese ADD COLUMN valor NUMERIC(14,2);
ALTER TABLE imovel_habitese ADD COLUMN frente NUMERIC(10,2);
ALTER TABLE imovel_habitese ADD COLUMN fundos NUMERIC(10,2);
ALTER TABLE imovel_habitese ADD COLUMN lado_esquerdo NUMERIC(10,2);
ALTER TABLE imovel_habitese ADD COLUMN lado_direito NUMERIC(10,2);
ALTER TABLE imovel_habitese ADD COLUMN observacao TEXT;
ALTER TABLE imovel_habitese ADD COLUMN codigo_verificacao VARCHAR(32);
ALTER TABLE imovel_habitese ADD COLUMN situacao_fiscal VARCHAR(20)
    CHECK (situacao_fiscal IN ('PENDENTE','PAGA','ISENTA','CANCELADA'));

CREATE TABLE imovel_habitese_responsavel (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id    UUID NOT NULL REFERENCES tenant(id),
    habitese_id  UUID NOT NULL REFERENCES imovel_habitese(id) ON DELETE CASCADE,
    ordem        SMALLINT NOT NULL CHECK (ordem IN (1,2)),
    nome         VARCHAR(200) NOT NULL,
    profissao    VARCHAR(100),
    documento    VARCHAR(50),
    UNIQUE (habitese_id, ordem)
);
SELECT aplicar_isolamento_tenant('imovel_habitese_responsavel');

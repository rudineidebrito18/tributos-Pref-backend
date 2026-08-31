-- E4.8 — Tipos de alvará, histórico de valor e campos auditados do alvará.

ALTER TABLE iss_tipo_alvara ADD COLUMN ano_vigencia SMALLINT;
ALTER TABLE iss_tipo_alvara ADD COLUMN identificacao_modelo_documento VARCHAR(100);
ALTER TABLE iss_tipo_alvara ADD COLUMN permite_valor_dinamico BOOLEAN NOT NULL DEFAULT false;
ALTER TABLE iss_tipo_alvara ADD COLUMN permite_calculo_valor BOOLEAN NOT NULL DEFAULT false;
ALTER TABLE iss_tipo_alvara ADD COLUMN unidade_medida_descritivo VARCHAR(100);
ALTER TABLE iss_tipo_alvara ADD COLUMN habilitar_validade BOOLEAN NOT NULL DEFAULT true;
ALTER TABLE iss_tipo_alvara ADD COLUMN habilitar_calculo_vencimento BOOLEAN NOT NULL DEFAULT false;
ALTER TABLE iss_tipo_alvara ADD COLUMN base_vencimento VARCHAR(5) CHECK (base_vencimento IN ('MES','DIAS'));
ALTER TABLE iss_tipo_alvara ADD COLUMN dias_meses_vencimento INTEGER;
ALTER TABLE iss_tipo_alvara ADD COLUMN titulo VARCHAR(200);
ALTER TABLE iss_tipo_alvara ADD COLUMN secretaria VARCHAR(200);
ALTER TABLE iss_tipo_alvara ADD COLUMN cargo VARCHAR(200);
ALTER TABLE iss_tipo_alvara ADD COLUMN assinatura_documento_id UUID REFERENCES documento(id);

CREATE TABLE iss_valor_alvara (
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id      UUID NOT NULL REFERENCES tenant(id),
    tipo_alvara_id UUID NOT NULL REFERENCES iss_tipo_alvara(id) ON DELETE CASCADE,
    ano_vigencia   SMALLINT NOT NULL,
    valor          NUMERIC(14,2) NOT NULL,
    usuario_id     UUID REFERENCES usuario(id),
    atualizado_em  TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, tipo_alvara_id, ano_vigencia)
);
SELECT aplicar_isolamento_tenant('iss_valor_alvara');

ALTER TABLE iss_alvara ADD COLUMN valor_por_unidade NUMERIC(14,2);
ALTER TABLE iss_alvara ADD COLUMN unidade_medida_descritivo VARCHAR(100);
ALTER TABLE iss_alvara ADD COLUMN qtd_unidade_medida NUMERIC(14,4);
ALTER TABLE iss_alvara ADD COLUMN documento_html TEXT;
ALTER TABLE iss_alvara ADD COLUMN responsavel_tecnico VARCHAR(200);
ALTER TABLE iss_alvara ADD COLUMN inscricao_conselho_rt VARCHAR(50);
ALTER TABLE iss_alvara ADD COLUMN motivo_cancelamento TEXT;
ALTER TABLE iss_alvara ADD COLUMN observacao TEXT;

ALTER TABLE iss_alvara DROP CONSTRAINT IF EXISTS iss_alvara_situacao_fiscal_check;
ALTER TABLE iss_alvara ADD CONSTRAINT iss_alvara_situacao_fiscal_check
    CHECK (situacao_fiscal IN ('PENDENTE','PAGA','ISENTA','CANCELADA'));
UPDATE iss_alvara SET situacao_fiscal = 'PENDENTE' WHERE situacao_fiscal = 'IRREGULAR';
UPDATE iss_alvara SET situacao_fiscal = 'PAGA'     WHERE situacao_fiscal = 'REGULAR';

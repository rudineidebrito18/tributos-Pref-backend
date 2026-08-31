-- E3.5 — Tipo de cálculo de guia ITBI e campos auditados do tipo de guia.

CREATE TABLE itbi_tipo_calculo_guia (
    id        UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL REFERENCES tenant(id),
    descricao VARCHAR(150) NOT NULL,
    UNIQUE (tenant_id, descricao)
);

SELECT aplicar_isolamento_tenant('itbi_tipo_calculo_guia');

ALTER TABLE itbi_tipo_guia ADD COLUMN tipo_calculo_id UUID REFERENCES itbi_tipo_calculo_guia(id);
ALTER TABLE itbi_tipo_guia ADD COLUMN permite_desconto BOOLEAN NOT NULL DEFAULT false;
ALTER TABLE itbi_tipo_guia ADD COLUMN habilita_calculo_valor BOOLEAN NOT NULL DEFAULT false;
ALTER TABLE itbi_tipo_guia ADD COLUMN valor NUMERIC(14, 2) NOT NULL DEFAULT 0;
ALTER TABLE itbi_tipo_guia ADD COLUMN valor_parcela NUMERIC(14, 2);
ALTER TABLE itbi_tipo_guia ADD COLUMN secretaria VARCHAR(200);
ALTER TABLE itbi_tipo_guia ADD COLUMN cargo VARCHAR(200);
ALTER TABLE itbi_tipo_guia ADD COLUMN assinatura_documento_id UUID REFERENCES documento(id);

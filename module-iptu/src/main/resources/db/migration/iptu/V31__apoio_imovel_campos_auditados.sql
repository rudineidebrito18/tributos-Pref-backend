-- E3.4 — Campos auditados de destinação e tipo de habite-se.

ALTER TABLE imovel_destinacao ADD COLUMN tipo_imovel_id UUID REFERENCES imovel_tipo(id);
ALTER TABLE imovel_destinacao ADD COLUMN aliquota_iptu NUMERIC(10, 6) NOT NULL DEFAULT 0;

ALTER TABLE imovel_habitese_tipo ADD COLUMN titulo VARCHAR(200);
ALTER TABLE imovel_habitese_tipo ADD COLUMN permite_desconto BOOLEAN NOT NULL DEFAULT false;
ALTER TABLE imovel_habitese_tipo ADD COLUMN habilita_calculo_valor BOOLEAN NOT NULL DEFAULT false;
ALTER TABLE imovel_habitese_tipo ADD COLUMN valor NUMERIC(14, 2) NOT NULL DEFAULT 0;
ALTER TABLE imovel_habitese_tipo ADD COLUMN secretaria VARCHAR(200);
ALTER TABLE imovel_habitese_tipo ADD COLUMN cargo VARCHAR(200);
ALTER TABLE imovel_habitese_tipo ADD COLUMN assinatura_documento_id UUID REFERENCES documento(id);

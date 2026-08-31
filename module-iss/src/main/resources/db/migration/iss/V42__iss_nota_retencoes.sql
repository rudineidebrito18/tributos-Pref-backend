-- E4.7 — Retenções federais e ISS retido na fonte por nota fiscal.

ALTER TABLE iss_nota_fiscal ADD COLUMN valor_ir     NUMERIC(14,2) NOT NULL DEFAULT 0;
ALTER TABLE iss_nota_fiscal ADD COLUMN valor_pis    NUMERIC(14,2) NOT NULL DEFAULT 0;
ALTER TABLE iss_nota_fiscal ADD COLUMN valor_cofins NUMERIC(14,2) NOT NULL DEFAULT 0;
ALTER TABLE iss_nota_fiscal ADD COLUMN valor_csll   NUMERIC(14,2) NOT NULL DEFAULT 0;
ALTER TABLE iss_nota_fiscal ADD COLUMN valor_inss   NUMERIC(14,2) NOT NULL DEFAULT 0;
ALTER TABLE iss_nota_fiscal ADD COLUMN iss_retido_fonte BOOLEAN NOT NULL DEFAULT false;

-- E3.1 — Atributos fiscais do bairro (zona fiscal IPTU e valor de terreno de referência).
ALTER TABLE bairro ADD COLUMN zona_fiscal_id UUID REFERENCES iptu_zona_fiscal(id);
ALTER TABLE bairro ADD COLUMN valor_terreno NUMERIC(14, 2);

-- E3.4/E3.5 — Documentos institucionais (assinaturas de catálogo) sem pessoa vinculada.
ALTER TABLE documento ALTER COLUMN pessoa_id DROP NOT NULL;

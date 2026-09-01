ALTER TABLE imovel_certidao_negativa ADD COLUMN situacao_cnd_id UUID REFERENCES iss_situacao_cnd(id);
ALTER TABLE imovel_certidao_negativa ADD COLUMN observacao TEXT;

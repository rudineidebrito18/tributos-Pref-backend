ALTER TABLE iss_contribuinte ADD COLUMN nome_fantasia VARCHAR(200);
ALTER TABLE iss_contribuinte ADD COLUMN inscricao_estadual VARCHAR(30);
ALTER TABLE iss_contribuinte ADD COLUMN contato VARCHAR(200);
ALTER TABLE iss_contribuinte ADD COLUMN telefone2 VARCHAR(20);
ALTER TABLE iss_contribuinte ADD COLUMN email_nota VARCHAR(255);
ALTER TABLE iss_contribuinte ADD COLUMN usuario_id UUID REFERENCES usuario(id);

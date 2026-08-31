-- E2.6 — Perfil do usuário: nome exibido e foto (documento institucional).
ALTER TABLE usuario ADD COLUMN nome VARCHAR(200);
ALTER TABLE usuario ADD COLUMN foto_documento_id UUID REFERENCES documento(id);

-- E2.7 — Documentos do sistema: categorias e compartilhamento entre usuários.
CREATE TABLE documento_categoria (
    id        UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL REFERENCES tenant(id),
    nome      VARCHAR(100) NOT NULL,
    criado_em TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, nome)
);
SELECT aplicar_isolamento_tenant('documento_categoria');

ALTER TABLE documento ADD COLUMN categoria_id UUID REFERENCES documento_categoria(id);
ALTER TABLE documento ADD COLUMN titulo VARCHAR(200);

CREATE TABLE documento_compartilhamento (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id    UUID NOT NULL REFERENCES tenant(id),
    documento_id UUID NOT NULL REFERENCES documento(id) ON DELETE CASCADE,
    usuario_id   UUID NOT NULL REFERENCES usuario(id),
    criado_em    TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (documento_id, usuario_id)
);
SELECT aplicar_isolamento_tenant('documento_compartilhamento');

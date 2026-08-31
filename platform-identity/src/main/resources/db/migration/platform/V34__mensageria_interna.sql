CREATE TABLE mensagem_interna (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id     UUID NOT NULL REFERENCES tenant(id),
    remetente_id  UUID NOT NULL REFERENCES usuario(id),
    assunto       VARCHAR(200) NOT NULL,
    corpo         TEXT NOT NULL,
    criado_em     TIMESTAMPTZ NOT NULL DEFAULT now()
);
SELECT aplicar_isolamento_tenant('mensagem_interna');

CREATE TABLE mensagem_interna_destinatario (
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id      UUID NOT NULL REFERENCES tenant(id),
    mensagem_id    UUID NOT NULL REFERENCES mensagem_interna(id) ON DELETE CASCADE,
    destinatario_id UUID NOT NULL REFERENCES usuario(id),
    lida_em        TIMESTAMPTZ,
    arquivada_em   TIMESTAMPTZ,
    UNIQUE (mensagem_id, destinatario_id)
);
SELECT aplicar_isolamento_tenant('mensagem_interna_destinatario');

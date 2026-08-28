-- Sessões de refresh token — só o hash SHA-256 é gravado (ver
-- br.com.tributos.identity.application.RefreshTokenFactory). token_hash tem 64 caracteres
-- porque SHA-256 em hexadecimal sempre produz exatamente esse tamanho.
CREATE TABLE refresh_token (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    usuario_id   UUID NOT NULL REFERENCES usuario(id) ON DELETE CASCADE,
    tenant_id    UUID NOT NULL REFERENCES tenant(id),
    token_hash   VARCHAR(64) NOT NULL UNIQUE,
    criado_em    TIMESTAMPTZ NOT NULL DEFAULT now(),
    expira_em    TIMESTAMPTZ NOT NULL,
    revogado_em  TIMESTAMPTZ
);
SELECT aplicar_isolamento_tenant('refresh_token');

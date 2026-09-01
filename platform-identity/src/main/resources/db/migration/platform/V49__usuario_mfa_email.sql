-- Código MFA por e-mail é de uso único e expira; TOTP continua usando apenas mfa_secret.
ALTER TABLE usuario ADD COLUMN mfa_codigo_expira_em TIMESTAMPTZ;

CREATE TABLE configuracao_pix_bb (
    id                          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id                   UUID NOT NULL REFERENCES tenant(id),
    ambiente                    VARCHAR(20) NOT NULL
                                CHECK (ambiente IN ('SANDBOX','HOMOLOGACAO','PRODUCAO')),
    ativo                       BOOLEAN NOT NULL DEFAULT false,
    client_id                   TEXT NOT NULL,
    client_secret_cifrado       TEXT NOT NULL,
    developer_application_key   TEXT NOT NULL,
    escopos                     VARCHAR(500) NOT NULL,
    numero_convenio             VARCHAR(6)  NOT NULL,
    chave_pix                   VARCHAR(77) NOT NULL,
    indicador_codigo_barras     CHAR(1) NOT NULL DEFAULT 'N' CHECK (indicador_codigo_barras IN ('S','N')),
    certificado_path            TEXT,
    certificado_senha_cifrada   TEXT,
    webhook_url                 TEXT,
    webhook_token_cifrado       TEXT,
    criado_em                   TIMESTAMPTZ NOT NULL DEFAULT now(),
    atualizado_em               TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, ambiente)
);
SELECT aplicar_isolamento_tenant('configuracao_pix_bb');

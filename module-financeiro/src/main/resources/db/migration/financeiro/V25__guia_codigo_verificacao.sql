ALTER TABLE guia_arrecadacao ADD COLUMN codigo_verificacao VARCHAR(32);

CREATE UNIQUE INDEX uq_guia_codigo_verificacao
    ON guia_arrecadacao(codigo_verificacao)
    WHERE codigo_verificacao IS NOT NULL;

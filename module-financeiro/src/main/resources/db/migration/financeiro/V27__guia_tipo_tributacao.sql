ALTER TABLE guia_arrecadacao
    ADD COLUMN tipo_tributacao VARCHAR(20) NOT NULL DEFAULT 'TRIBUTAVEL'
        CHECK (tipo_tributacao IN ('TRIBUTAVEL', 'ISENTO', 'IMUNE'));

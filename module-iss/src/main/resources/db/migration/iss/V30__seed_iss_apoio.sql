-- E3.3 — Seed de grupos LC 116/2003 e locais de incidência para tenant demo.

DO $$
DECLARE
    v_tenant UUID;
    i        INT;
BEGIN
    SELECT id INTO v_tenant FROM tenant WHERE slug = 'demo';

    IF v_tenant IS NOT NULL THEN
        FOR i IN 1..40 LOOP
            INSERT INTO iss_grupo_servico (tenant_id, codigo, descricao)
            VALUES (v_tenant, i::TEXT, 'Grupo ' || i || ' — LC 116/2003')
            ON CONFLICT (tenant_id, codigo) DO NOTHING;
        END LOOP;

        INSERT INTO iss_local_incidencia (tenant_id, descricao) VALUES
            (v_tenant, 'ESTABELECIMENTO_PRESTADOR'),
            (v_tenant, 'LOCAL_DA_PRESTACAO'),
            (v_tenant, 'DOMICILIO_TOMADOR')
        ON CONFLICT (tenant_id, descricao) DO NOTHING;
    END IF;
END $$;

-- Parametrização IPTU inicial para o tenant demo (Sprint 7).

DO $$
DECLARE
    v_tenant_id UUID;
BEGIN
    SELECT id INTO v_tenant_id FROM tenant WHERE slug = 'demo';

    INSERT INTO iptu_zona_fiscal (id, tenant_id, nome, fator_valorizacao) VALUES
        ('80000006-0000-4000-8000-000000000001', v_tenant_id, 'CENTRO', 1.2),
        ('80000006-0000-4000-8000-000000000002', v_tenant_id, 'PERIFERIA', 1.0)
    ON CONFLICT DO NOTHING;

    INSERT INTO iptu_valor_terreno_m2 (tenant_id, zona_fiscal_id, exercicio, valor_m2) VALUES
        (v_tenant_id, '80000006-0000-4000-8000-000000000001', 2025, 500.00),
        (v_tenant_id, '80000006-0000-4000-8000-000000000002', 2025, 200.00)
    ON CONFLICT DO NOTHING;

    INSERT INTO iptu_aliquota (tenant_id, exercicio, destinacao_id, zona_fiscal_id, aliquota) VALUES
        (v_tenant_id, 2025, '80000003-0000-4000-8000-000000000001', '80000006-0000-4000-8000-000000000001', 0.015000),
        (v_tenant_id, 2025, '80000003-0000-4000-8000-000000000001', '80000006-0000-4000-8000-000000000002', 0.015000),
        (v_tenant_id, 2025, '80000003-0000-4000-8000-000000000002', '80000006-0000-4000-8000-000000000001', 0.015000),
        (v_tenant_id, 2025, '80000003-0000-4000-8000-000000000002', '80000006-0000-4000-8000-000000000002', 0.015000),
        (v_tenant_id, 2025, '80000003-0000-4000-8000-000000000003', '80000006-0000-4000-8000-000000000001', 0.015000),
        (v_tenant_id, 2025, '80000003-0000-4000-8000-000000000003', '80000006-0000-4000-8000-000000000002', 0.015000)
    ON CONFLICT DO NOTHING;
END $$;

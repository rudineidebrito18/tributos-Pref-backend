-- Tipos de alvará iniciais para o tenant demo (Sprint 5).

DO $$
DECLARE
    v_tenant_id UUID;
BEGIN
    SELECT id INTO v_tenant_id FROM tenant WHERE slug = 'demo';

    INSERT INTO iss_tipo_alvara (id, tenant_id, nome, valor_base, dias_validade) VALUES
        ('g0000001-0000-4000-8000-000000000001', v_tenant_id, 'Funcionamento', 150.00, 365),
        ('g0000001-0000-4000-8000-000000000002', v_tenant_id, 'Provisório', 80.00, 90)
    ON CONFLICT DO NOTHING;
END $$;

-- Catálogos IPTU iniciais para o tenant demo (Sprint 6).

DO $$
DECLARE
    v_tenant_id UUID;
BEGIN
    SELECT id INTO v_tenant_id FROM tenant WHERE slug = 'demo';

    INSERT INTO imovel_tipo (id, tenant_id, nome) VALUES
        ('80000001-0000-4000-8000-000000000001', v_tenant_id, 'PREDIAL'),
        ('80000001-0000-4000-8000-000000000002', v_tenant_id, 'TERRITORIAL'),
        ('80000001-0000-4000-8000-000000000003', v_tenant_id, 'RURAL')
    ON CONFLICT DO NOTHING;

    INSERT INTO imovel_tipo_edificacao (id, tenant_id, nome) VALUES
        ('80000002-0000-4000-8000-000000000001', v_tenant_id, 'ALVENARIA'),
        ('80000002-0000-4000-8000-000000000002', v_tenant_id, 'MADEIRA')
    ON CONFLICT DO NOTHING;

    INSERT INTO imovel_destinacao (id, tenant_id, nome) VALUES
        ('80000003-0000-4000-8000-000000000001', v_tenant_id, 'RESIDENCIAL'),
        ('80000003-0000-4000-8000-000000000002', v_tenant_id, 'COMERCIAL'),
        ('80000003-0000-4000-8000-000000000003', v_tenant_id, 'MISTO')
    ON CONFLICT DO NOTHING;

    INSERT INTO imovel_tipo_limitacao (id, tenant_id, nome) VALUES
        ('80000004-0000-4000-8000-000000000001', v_tenant_id, 'NENHUMA'),
        ('80000004-0000-4000-8000-000000000002', v_tenant_id, 'APP')
    ON CONFLICT DO NOTHING;

    INSERT INTO imovel_habitese_tipo (id, tenant_id, nome) VALUES
        ('80000005-0000-4000-8000-000000000001', v_tenant_id, 'DEFINITIVO'),
        ('80000005-0000-4000-8000-000000000002', v_tenant_id, 'PARCIAL')
    ON CONFLICT DO NOTHING;
END $$;

DO $$
DECLARE
    v_tenant_id UUID;
BEGIN
    SELECT id INTO v_tenant_id FROM tenant WHERE slug = 'demo';

    INSERT INTO itbi_tipo_guia (id, tenant_id, nome, aliquota) VALUES
        ('a1000001-0000-4000-8000-000000000001', v_tenant_id, 'COMPRA_E_VENDA', 0.020000),
        ('a1000001-0000-4000-8000-000000000002', v_tenant_id, 'DOACAO', 0.040000)
    ON CONFLICT DO NOTHING;

    INSERT INTO itbi_natureza_transmissao (id, tenant_id, nome) VALUES
        ('a1000002-0000-4000-8000-000000000001', v_tenant_id, 'COMPRA_E_VENDA'),
        ('a1000002-0000-4000-8000-000000000002', v_tenant_id, 'DOACAO'),
        ('a1000002-0000-4000-8000-000000000003', v_tenant_id, 'PERMUTA')
    ON CONFLICT DO NOTHING;
END $$;

-- E3.5 — Tipos de cálculo ITBI iniciais para o tenant demo.

DO $$
DECLARE
    v_tenant_id UUID;
BEGIN
    SELECT id INTO v_tenant_id FROM tenant WHERE slug = 'demo';

    INSERT INTO itbi_tipo_calculo_guia (id, tenant_id, descricao) VALUES
        ('a1000003-0000-4000-8000-000000000001', v_tenant_id, 'PERCENTUAL_SOBRE_BASE'),
        ('a1000003-0000-4000-8000-000000000002', v_tenant_id, 'VALOR_FIXO')
    ON CONFLICT DO NOTHING;
END $$;
